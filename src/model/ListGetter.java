package model;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class ListGetter {

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

	public static StockBag getScreen(String url) {
		StockBag stockBag = new StockBag();

		final int PAGES = getPages(url);

		final int MAX_THREADS = 3 * (PAGES / 20);

		ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

		for (int page = 1; page <= PAGES; page += 20) {

			Connection html = Jsoup.connect(url + "&r=" + String.valueOf(page));

			Retriever thread = new Retriever(html, stockBag);

			pool.execute(thread);
		}

		while (!pool.isTerminated()) {
			pool.shutdown();
		}

		return stockBag;
	}
}
