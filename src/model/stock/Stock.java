package model.stock;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Stock implements Serializable {

    private Map<String, Data> map;

    public Stock() {
        this.map = Collections.synchronizedMap(new LinkedHashMap<>());
    }

    public void display() {
        System.out.println(map);
    }

    public Map<String, Data> getMap() {
        return map;
    }

}
