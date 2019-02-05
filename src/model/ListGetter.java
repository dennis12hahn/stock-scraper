package model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    public static void exportToExcel(String filePath, StockBag stockBag) {
	XSSFWorkbook workbook = new XSSFWorkbook();
	XSSFSheet sheet = workbook.createSheet("Sheet1");

	CellStyle percentStyle = workbook.createCellStyle();
	percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

	Row[] rows = new Row[stockBag.getStocks().size() + 1];

	List<String> keys = new ArrayList<String>(stockBag.getStocks().get(0).getMap().keySet());

	for (int i = 0; i < rows.length; i++) {

	    rows[i] = sheet.createRow((short) i);

	    for (int j = 0; j < keys.size(); j++) {

		Cell cell = rows[i].createCell(j);

		if (i == 0) {
		    cell.setCellValue(keys.get(j));
		} else {
		    Data d = stockBag.getStocks().get(i - 1).getMap().get(keys.get(j));

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
}
