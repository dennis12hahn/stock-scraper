package model.retrievers;

import model.MyUtils;
import model.stock.Data;
import model.stock.Stock;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DataRetriever implements Runnable {

    private Connection page;
    private Stock stock;

    public DataRetriever(Stock stock) {
        this.stock = stock;
        String symbol = stock.getMap().get("Symbol").getStrVal();
        if (symbol.contains(".")) {
            symbol = symbol.replace(".", "-");
        }
        this.page = Jsoup.connect("https://finviz.com/quote.ashx?t=" + symbol);
    }

    @Override
    public void run() {
        pullAllData();
    }

    private void pullAllData() {
        addSectorInfo();
        addData();
    }

    private void addData() {
        Elements table;
        Elements rows;
        try {
            table = page.get().select("table").get(8).select("tbody");
            rows = table.select("tr");
            String key;
            String val;

            for (int i = 1; i < 12; i += 2)
                for (Element row : rows) {

                    key = row.select("td").get(i - 1).html();
                    val = row.select("td").get(i).select("b").html();

                    if (val.contains("small")) {
                        val = row.select("td").get(i).select("b").select("small").html();
                    }

                    if (val.contains("span")) {
                        val = row.select("td").get(i).select("b").select("span").html();
                    }

                    stock.getMap().put(key, new Data(key, MyUtils.cleanText(val)));
                }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addSectorInfo() {
        Elements sectorInfo;
        try {
            sectorInfo = page.get().select("table").get(6).select("tbody").select("tr").get(2).select("a");

            for (int i = 0; i < sectorInfo.size(); i++) {

                String key;

                if (i == 0) {
                    key = "Sector";
                } else if (i == 1) {
                    key = "Industry";
                } else {
                    key = "Country";
                }

                String val = MyUtils.cleanText(sectorInfo.get(i).html());
                stock.getMap().put(key, new Data(key, val));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
