package model;

public abstract class MyUtils {

    public static String cleanText(String str) {
        str = str.replaceAll(",", "");
        str = str.replaceAll("amp;", "");
        return str;
    }
}
