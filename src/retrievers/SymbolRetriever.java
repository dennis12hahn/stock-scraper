package retrievers;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jsoup.Connection;
import org.jsoup.select.Elements;

public class SymbolRetriever extends Thread {

	private Thread thread;
	private Connection html;
	private static final String cssSelector = "#screener-content > table > tbody > tr:nth-child(4) > td > table > tbody";
	private static CopyOnWriteArrayList<String> symbols = new CopyOnWriteArrayList<String>();
	private static CopyOnWriteArrayList<String> companies = new CopyOnWriteArrayList<String>();

	public SymbolRetriever(Connection html) {
		this.html = html;
	}

	@Override
	public void run() {
		Elements rows = null;
		try {
			rows = html.get().select(cssSelector).select("tr");
			for (int i = 1; i < rows.size(); i++) {
				String symbol = rows.get(i).select("td").get(1).selectFirst("a").html();
				String company = rows.get(i).select("td").get(2).selectFirst("a").html();
				symbols.add(symbol);
				companies.add(company);
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

	public static CopyOnWriteArrayList<String> getSymbols() {
		return symbols;
	}

	public static CopyOnWriteArrayList<String> getCompanies() {
		return companies;
	}

	public Thread getThread() {
		return thread;
	}

}
