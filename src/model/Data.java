package model;

import java.io.Serializable;

public class Data implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String strVal, type;
    private double dblVal;
    private boolean percentage;

    public Data(String key, String val) {
	this.strVal = val;
	this.percentage = false;

	switch (key) {
	case "Symbol":
	case "Company":
	case "Sector":
	case "Industry":
	case "Country":
	case "Index":
	case "Optionable":
	case "Shortable":
	case "Earnings":
	case "52W Range":
	case "Volatility":
	    this.setType("string");
	    break;
	default:
	    this.setType("double");
	    setDblVal(val);
	}
    }

    private void handlePercent(String val) {
	int counter = 0;
	for (int i = 0; i < val.length(); i++) {
	    if (val.charAt(i) == '%') {
		counter++;
	    }
	}
	if (counter > 1) {
	    val = "0";
	}
	val = val.replaceAll("%", "");
	this.dblVal = Double.parseDouble(val);
	this.dblVal /= 100;
	this.percentage = true;
    }

    public double getDblVal() {
	return dblVal;
    }

    public void setDblVal(String val) {
	char lastChar = val.charAt(val.length() - 1);

	if ((lastChar == 'T') || (lastChar == 'B') || (lastChar == 'M') || (lastChar == 'K') || (lastChar == '%')) {
	    val = val.substring(0, val.length() - 1);
	    this.dblVal = Double.parseDouble(val);
	}

	switch (lastChar) {
	case 'T':
	    this.dblVal *= 1000000000000.0;
	    break;
	case 'B':
	    this.dblVal *= 1000000000.0;
	    break;
	case 'M':
	    this.dblVal *= 1000000.0;
	    break;
	case 'K':
	    this.dblVal *= 1000.0;
	    break;
	case '%':
	    handlePercent(val);
	    break;
	default:
	    try {
		this.dblVal = Double.parseDouble(val);
	    } catch (NumberFormatException e) {
		; // Do nothing
	    }
	    break;
	}
    }

    public String getStrVal() {
	return strVal;
    }

    public void setStrVal(String strVal) {
	this.strVal = strVal;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public boolean isPercentage() {
	return percentage;
    }

    public void setPercentage(boolean percentage) {
	this.percentage = percentage;
    }

    @Override
    public String toString() {
	return "dblVal: " + dblVal + ", strVal: " + strVal + ", type: " + type;
    }

}
