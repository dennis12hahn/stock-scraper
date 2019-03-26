package test;

import model.ListGetter;
import model.Stock;
import model.StockBag;

public class Demo {
	public static void main(String[] args) {
		String url = "https://finviz.com/screener.ashx?v=111&f=idx_dji";

		StockBag djia = ListGetter.getScreen(url);

		for (Stock s : djia.getStocks()) {
			System.out.println(s);
		}

	}

}
