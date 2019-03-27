package model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StockBag {
	private List<Stock> stocks;

	public StockBag() {
		this.setStocks(Collections.synchronizedList(new ArrayList<Stock>()));
	}

	public List<Stock> getStocks() {
		return stocks;
	}

	public void exportToCsv(String filePath) {
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(filePath, true), true);
			List<String> keys = new ArrayList<String>(stocks.get(0).getMap().keySet());

			for (int i = 0; i < keys.size(); i++) {
				printWriter.print(keys.get(i) + ",");
			}
			printWriter.print("\n");

			for (int i = 0; i < stocks.size(); i++) {
				for (int j = 0; j < keys.size(); j++) {
					Data d = stocks.get(i).getMap().get(keys.get(j));

					if (d.getType().equals("double")) {
						printWriter.print(d.getDblVal() + ",");
					} else {
						printWriter.print(d.getStrVal() + ",");
					}

				}
				printWriter.print("\n");
			}

			printWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}
}
