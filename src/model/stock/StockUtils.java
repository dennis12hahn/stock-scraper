package model.stock;

import model.retrievers.DataRetriever;
import model.retrievers.ListRetriever;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class StockUtils {

    private static final int THREAD_LIMIT = 150;

    public static StockBag getAll() {
        String url = "https://finviz.com/screener.ashx?v=111";
        return getScreen(url);
    }

    public static StockBag getSP500() {
        String url = "https://finviz.com/screener.ashx?v=111&f=idx_sp500";
        return getScreen(url);
    }

    public static StockBag getDJIA() {
        String url = "https://finviz.com/screener.ashx?v=111&f=idx_dji";
        return getScreen(url);
    }

    public static StockBag getScreen(String url) {
        StockBag stockBag = new StockBag();

        fillList(stockBag, url);
        fillData(stockBag);

        return stockBag;
    }

    private static void fillData(StockBag stockBag) {
        int numThreads = stockBag.getStocks().size();

        if (numThreads > THREAD_LIMIT) {
            numThreads = THREAD_LIMIT;
        }

        ExecutorService pool = Executors.newFixedThreadPool(numThreads);

        for (Stock s : stockBag.getStocks()) {
            DataRetriever dataRetriever = new DataRetriever(s);
            pool.execute(dataRetriever);
        }

        while (!pool.isTerminated()) {
            pool.shutdown();
        }
    }

    private static void fillList(StockBag stockBag, String url) {
        int pages = getPages(url);
        int numThreads = pages / 20;

        if (numThreads > THREAD_LIMIT) {
            numThreads = THREAD_LIMIT;
        }

        ExecutorService pool = Executors.newFixedThreadPool(numThreads);

        for (int i = 1; i <= pages; i += 20) {
            Connection page = Jsoup.connect(url + "&r=" + String.valueOf(i));
            ListRetriever listRetriever = new ListRetriever(stockBag, page);
            pool.execute(listRetriever);
        }

        while (!pool.isTerminated()) {
            pool.shutdown();
        }
    }

    private static int getPages(String url) {
        Connection html = Jsoup.connect(url);
        try {
            String s = html.get().select("td.count-text:nth-child(1)").text();
            int i = Integer.parseInt(s.split("\\s")[1]);
            return i + (i % 20);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
