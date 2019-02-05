package model;

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

    public void setStocks(List<Stock> stocks) {
	this.stocks = stocks;
    }
}
