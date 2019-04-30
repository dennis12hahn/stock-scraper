package model.retrievers;

import model.stock.Data;
import model.stock.Stock;
import model.stock.StockBag;
import org.jsoup.Connection;
import org.jsoup.select.Elements;

import java.io.IOException;

import static model.MyUtils.cleanText;

public class ListRetriever implements Runnable {

    private static final String cssSelector = "#screener-content > table > tbody > tr:nth-child(4) > td > table > tbody";
    private static StockBag stockBag;
    private Connection page;

    public ListRetriever(StockBag stockBag, Connection page) {
        ListRetriever.stockBag = stockBag;
        this.page = page;
    }

    @Override
    public void run() {
        Elements rows;
        try {
            rows = page.get().select(cssSelector).select("tr");
            for (int i = 1; i < rows.size(); i++) {
                Stock temp = new Stock();

                String symbol = cleanText(rows.get(i).select("td").get(1).selectFirst("a").html());
                String company = cleanText(rows.get(i).select("td").get(2).selectFirst("a").html());

                temp.getMap().put("Symbol", new Data("Symbol", symbol));
                temp.getMap().put("Company", new Data("Company", company));
                stockBag.getStocks().add(temp);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
