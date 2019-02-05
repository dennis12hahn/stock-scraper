package model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Stock implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private LinkedHashMap<String, Data> map = new LinkedHashMap<String, Data>(79);

    public LinkedHashMap<String, Data> getMap() {
	return map;
    }

    public void setMap(LinkedHashMap<String, Data> map) {
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
