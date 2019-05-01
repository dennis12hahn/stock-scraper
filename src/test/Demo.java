package test;

import model.stock.StockBag;
import model.stock.StockUtils;

public class Demo {
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		StockBag all = StockUtils.getAll();
		long elapsed = System.currentTimeMillis() - start;
		System.out.println(elapsed);
		System.out.println(all.getStocks().size());
		all.exportToCsv("/home/dennis/Desktop/all.csv");
	}

}
