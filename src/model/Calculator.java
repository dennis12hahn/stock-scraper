package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Calculator {

	private CopyOnWriteArrayList<Stock> stocks;

	private ArrayList<String> sectors;
	private ArrayList<String> industries;

	public Calculator(CopyOnWriteArrayList<Stock> stocks) {
		this.stocks = stocks;
		this.sectors = getAllSectors();
		this.industries = getAllIndustries();
	}

	public ArrayList<Stock> lessThanAverage(String specifier, String key) {
		ArrayList<Stock> returnList = new ArrayList<Stock>();
		TreeMap<String, Double> map = null;

		if (specifier.equals("Sector")) {
			map = sectorAverages(key);
		} else if (specifier.equals("Industry")) {
			map = industryAverages(key);
		}

		for (Entry<String, Double> en : map.entrySet()) {
			for (Stock s : stocks) {
				if ((s.getMap().get(key).getDblVal() < en.getValue()) && !s.getMap().get(key).getStrVal().equals("-")) {
					if (s.getMap().get(key).getDblVal() < 1) {
						returnList.add(s);
					}
				}
			}
		}
		return returnList;
	}

	public TreeMap<String, Double> sectorAverages(String key) {
		TreeMap<String, Double> map = new TreeMap<String, Double>();

		for (String sector : sectors) {
			double sum = getSum("Sector", sector, key);
			double count = getCount("Sector", sector, key);
			map.put(sector, sum / count);
		}
		return map;
	}

	public TreeMap<String, Double> industryAverages(String key) {
		TreeMap<String, Double> map = new TreeMap<String, Double>();

		for (String industry : industries) {
			double sum = getSum("Industry", industry, key);
			double count = getCount("Industry", industry, key);
			map.put(industry, sum / count);
		}
		return map;
	}

	private double getCount(String key1, String group, String key2) {
		double count = 0;
		for (Stock s : stocks) {
			if (key1.equals("Sector") && isEtf(s)) {
				continue;
			}
			if (s.getMap().get(key1).getStrVal().equals(group)) {
				if (!s.getMap().get(key2).getStrVal().equals("-")) {
					count++;
				}
			}
		}
		return count;
	}

	private double getSum(String key1, String group, String key2) {
		double sum = 0;
		for (Stock s : stocks) {
			if (key1.equals("Sector") && isEtf(s)) {
				continue;
			}
			if (s.getMap().get(key1).getStrVal().equals(group)) {
				if (!s.getMap().get(key2).getStrVal().equals("-")) {
					sum += s.getMap().get(key2).getDblVal();
				}
			}
		}
		return sum;
	}

	private ArrayList<String> getAllIndustries() {
		ArrayList<String> industries = new ArrayList<String>();

		for (Stock s : stocks) {
			if (!industries.contains(s.getMap().get("Industry").getStrVal())) {
				industries.add(s.getMap().get("Industry").getStrVal());
			}
		}
		return industries;
	}
	
	private ArrayList<String> getAllSectors() {
		ArrayList<String> sectors = new ArrayList<String>();
		
		for (Stock s : stocks) {
			if (!sectors.contains(s.getMap().get("Sector").getStrVal())) {
				sectors.add(s.getMap().get("Sector").getStrVal());
			}
		}
		return sectors;
	}

	private boolean isEtf(Stock s) {
		return s.getMap().get("Industry").getStrVal().equals("Exchange Traded Funds");
	}

}
