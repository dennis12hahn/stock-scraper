package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import retrievers.DataRetriever;
import retrievers.SymbolRetriever;

public class StockReader {

	private ArrayList<String> keys;
	private CopyOnWriteArrayList<Stock> stocks;

	public StockReader() {
		this.keys = addKeys();
		this.stocks = new CopyOnWriteArrayList<Stock>();
	}

	private int getPages(String url) {
		Connection con = Jsoup.connect(url);
		try {
			String s = con.get().select("td.count-text:nth-child(1)").text();
			int i = Integer.parseInt(s.split("\\s")[1]);
			return i + (i % 20);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void importScreen(String url) {
		ExecutorService pool = Executors.newFixedThreadPool(50);
		final int PAGES = getPages(url);

		for (int page = 1; page <= PAGES; page += 20) {
			Connection html = Jsoup.connect(url + "&r=" + String.valueOf(page));
			SymbolRetriever thread = new SymbolRetriever(html);
			pool.execute(thread);
		}

		while (!pool.isTerminated()) {
			pool.shutdown();
		}
		getAllData(SymbolRetriever.getCompanies(), SymbolRetriever.getSymbols());
	}

	public void importAll() {
		ExecutorService pool = Executors.newFixedThreadPool(50);
		final int PAGES = getPages("https://finviz.com/screener.ashx");

		for (int page = 1; page <= PAGES; page += 20) {
			Connection html = Jsoup.connect("https://finviz.com/screener.ashx?v=111&r=" + String.valueOf(page));
			SymbolRetriever thread = new SymbolRetriever(html);
			pool.execute(thread);
		}

		while (!pool.isTerminated()) {
			pool.shutdown();
		}
		getAllData(SymbolRetriever.getCompanies(), SymbolRetriever.getSymbols());
	}

	private void getAllData(List<String> companies, List<String> symbols) {
		ExecutorService pool = Executors.newFixedThreadPool(50);
		String symbol = new String();

		for (int i = 0; i < symbols.size(); i++) {
			symbol = symbols.get(i);
			if (symbol.contains(".")) {
				symbol = symbol.replace(".", "-");
			}

			Connection html = Jsoup.connect("https://finviz.com/quote.ashx?t=" + symbol);
			DataRetriever thread = new DataRetriever(html, companies.get(i), symbols.get(i), this);
			pool.execute(thread);
		}

		while (!pool.isTerminated()) {
			pool.shutdown();
		}
	}

	public void exportToExcel(String filePath) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sheet1");

		CellStyle percentStyle = workbook.createCellStyle();
		percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

		Row[] rows = new Row[stocks.size() + 1];
		for (int i = 0; i < rows.length; i++) {

			rows[i] = sheet.createRow((short) i);

			for (int j = 0; j < keys.size(); j++) {

				Cell cell = rows[i].createCell(j);

				if (i == 0) {
					cell.setCellValue(keys.get(j));
				} else {
					Data d = stocks.get(i - 1).getMap().get(keys.get(j));

					if (d.getType().equals("double")) {
						cell.setCellValue(d.getDblVal());
					} else {
						cell.setCellValue(d.getStrVal());
					}

					if (d.isPercentage()) {
						cell.setCellStyle(percentStyle);
					}
				}
			}
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(filePath);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> addKeys() {
		String tempUrl = "https://finviz.com/quote.ashx?t=aapl";
		Connection conn = Jsoup.connect(tempUrl);
		ArrayList<String> keys = new ArrayList<String>();
		keys.add("Company");
		keys.add("Symbol");
		keys.add("Sector");
		keys.add("Industry");
		keys.add("Country");
		try {
			Elements table = conn.get().select("table").get(8).select("tbody");
			Elements rows = table.select("tr");
			String temp = new String();
			for (int i = 0; i < 12; i += 2) {
				for (int j = 0; j < rows.size(); j++) {
					temp = rows.get(j).select("td").get(i).html();
					keys.add(temp);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		keys.addAll(keys.indexOf("Volatility"), Arrays.asList("Volatility(Week)", "Volatility(Month)"));
		keys.remove("Volatility");
		return keys;
	}

	public void save(String filePath) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
			oos.writeObject(stocks);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void load(String filePath) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			stocks = (CopyOnWriteArrayList<Stock>) ois.readObject();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public CopyOnWriteArrayList<Stock> getStocks() {
		return stocks;
	}

	public ArrayList<String> getKeys() {
		return keys;
	}
}
