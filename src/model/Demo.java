package model;

import java.util.ArrayList;

public class Demo {

	public static void main(String[] args) throws InterruptedException {
		StockReader stockReader = new StockReader();
		stockReader.load("C:\\Users\\Dennis\\Desktop\\2018-08-05.stocks");
		Calculator c = new Calculator(stockReader.getStocks());

		ArrayList<Stock> unders = c.lessThanAverage("Industry", "PEG");

		for (Stock s : unders) {
			System.out.println(s.getMap().get("PEG").getDblVal() + "\t" + s.getMap().get("Company").getStrVal());
		}
		System.out.println(unders.size());

		//		TreeMap<String, Double> ind = c.industryAverages("PEG");
		//
		//		for (Entry<String, Double> en : ind.entrySet()) {
		//			System.out.printf("%-60.60s %.2f%n", en.getKey(), en.getValue());
		//		}

	}

}
