package model.stock;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StockBag {
    private List<Stock> stocks;

    public StockBag() {
        this.stocks = Collections.synchronizedList(new ArrayList<>());
    }

    public void exportToCsv(String filePath) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(filePath, true), true);
            List<String> keys = new ArrayList<>(stocks.get(0).getMap().keySet());

            for (String key : keys) {
                printWriter.print(key + ",");
            }
            printWriter.print("\n");

            for (Stock stock : stocks) {
                for (String key : keys) {
                    Data d = stock.getMap().get(key);

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

    public List<Stock> getStocks() {
        return stocks;
    }
}
