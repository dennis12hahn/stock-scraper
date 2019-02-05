package retrievers;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.select.Elements;

import model.Data;
import model.Stock;
import model.StockReader;

public class DataRetriever extends Thread {

	private Thread thread;
	private Connection html;
	private ArrayList<String> vals;
	private StockReader stockReader;

	public DataRetriever(Connection html, String company, String symbol, StockReader stockReader) {
		this.html = html;
		this.vals = new ArrayList<String>();
		this.stockReader = stockReader;
		vals.add(company);
		vals.add(symbol);
	}

	@Override
	public void run() {
		addSectorInfo();
		addData();
		stockReader.getStocks().add(addVals());
	}

	@Override
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	private void addData() {
		Elements table;
		Elements rows;
		try {
			table = html.get().select("table").get(8).select("tbody");
			rows = table.select("tr");
			String temp = new String();
			for (int i = 1; i < 12; i += 2) {
				for (int j = 0; j < rows.size(); j++) {
					temp = rows.get(j).select("td").get(i).select("b").html();
					if (temp.contains("small")) {
						temp = rows.get(j).select("td").get(i).select("b").select("small").html();
					}
					if (temp.contains("span")) {
						temp = rows.get(j).select("td").get(i).select("b").select("span").html();
					}
					vals.add(temp);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void addSectorInfo() {
		Elements sectorInfo;
		try {
			sectorInfo = html.get().select("table").get(6).select("tbody").select("tr").get(2).select("a");
			for (int i = 0; i < sectorInfo.size(); i++) {
				vals.add(sectorInfo.get(i).html());
			}
		} catch (HttpStatusException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Stock addVals() {
		Stock stock = new Stock();

		for (int j = 0; j < vals.size(); j++) {
			vals.set(j, cleanText(vals.get(j)));
			if (stockReader.getKeys().get(j).equals("Volatility(Week)")) {
				handleVolatility(j, vals.remove(j), stock);
			}
			stock.getMap().put(stockReader.getKeys().get(j), new Data(stockReader.getKeys().get(j), vals.get(j)));
		}
		return stock;
	}

	private void handleVolatility(int j, String str, Stock stock) {
		String week = str.substring(0, str.indexOf(" "));
		String month = str.substring(str.indexOf(" ") + 1);
		vals.add(j, week);
		vals.add(j + 1, month);
	}

	private static String cleanText(String s) {
		return s.replaceAll(",", "").replaceAll("amp;", "");
	}

	public Thread getThread() {
		return thread;
	}

}
