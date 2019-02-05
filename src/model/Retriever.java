package model;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class Retriever extends Thread {

    private Thread thread;
    private Connection mainPage;
    private static final String cssSelector = "#screener-content > table > tbody > tr:nth-child(4) > td > table > tbody";
    private StockBag stockBag;

    public Retriever(Connection mainPage, StockBag stockBag) {
	this.mainPage = mainPage;
	this.stockBag = stockBag;
    }

    @Override
    public void run() {
	Elements rows = null;
	try {
	    rows = mainPage.get().select(cssSelector).select("tr");
	    for (int i = 1; i < rows.size(); i++) {
		Stock temp = new Stock();

		String symbol = rows.get(i).select("td").get(1).selectFirst("a").html();
		String company = rows.get(i).select("td").get(2).selectFirst("a").html();

		temp.getMap().put("Symbol", new Data("Symbol", symbol));
		temp.getMap().put("Company", new Data("Company", company));

		pullAllData(temp);

		this.stockBag.getStocks().add(temp);

	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void start() {
	if (thread == null) {
	    thread = new Thread(this);
	    thread.start();
	}
    }

    private void pullAllData(Stock stock) {
	String symbol = stock.getMap().get("Symbol").getStrVal();

	if (symbol.contains(".")) {
	    symbol = symbol.replace(".", "-");
	}

	Connection html = Jsoup.connect("https://finviz.com/quote.ashx?t=" + symbol);

	addSectorInfo(stock, html);
	addData(stock, html);
    }

    private void addData(Stock stock, Connection html) {
	Elements table;
	Elements rows;
	try {
	    table = html.get().select("table").get(8).select("tbody");
	    rows = table.select("tr");
	    String key = "";
	    String val = "";

	    for (int i = 1; i < 12; i += 2) {
		for (int j = 0; j < rows.size(); j++) {

		    key = rows.get(j).select("td").get(i - 1).html();

		    val = rows.get(j).select("td").get(i).select("b").html();
		    if (val.contains("small")) {
			val = rows.get(j).select("td").get(i).select("b").select("small").html();
		    }
		    if (val.contains("span")) {
			val = rows.get(j).select("td").get(i).select("b").select("span").html();
		    }

		    cleanText(val);
		    stock.getMap().put(key, new Data(key, val));
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    private void addSectorInfo(Stock stock, Connection html) {
	Elements sectorInfo;
	try {
	    sectorInfo = html.get().select("table").get(6).select("tbody").select("tr").get(2).select("a");

	    for (int i = 0; i < sectorInfo.size(); i++) {

		String key = "";

		if (i == 0) {
		    key = "Sector";
		} else if (i == 1) {
		    key = "Industry";
		} else {
		    key = "Country";
		}

		String val = cleanText(sectorInfo.get(i).html());
		stock.getMap().put(key, new Data(key, val));
	    }
	} catch (HttpStatusException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private static String cleanText(String str) {
	return str.replaceAll(",", "").replaceAll("amp;", "");
    }

    public Thread getThread() {
	return thread;
    }

}
