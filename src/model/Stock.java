package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

public class Stock implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, Data> map = new HashMap<String, Data>(78);

	public HashMap<String, Data> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Data> map) {
		this.map = map;
	}

	public void display() {
		for (Entry<String, Data> en : map.entrySet()) {
			System.out.println(en.getKey() + "\t\t" + en.getValue());
		}
	}

	@Override
	public String toString() {
		return "map: " + map;
	}

}
