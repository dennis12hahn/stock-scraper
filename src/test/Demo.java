package test;

import model.stock.Stock;
import model.stock.StockBag;
import model.stock.StockUtils;

public class Demo {
    public static void main(String[] args) {
        StockBag all = StockUtils.getAll();
        System.out.println(all.getStocks().size());
        all.exportToCsv("C:\\Users\\hahnd62\\Desktop\\all.csv");
    }

}
