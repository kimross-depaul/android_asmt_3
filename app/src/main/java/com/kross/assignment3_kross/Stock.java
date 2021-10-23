package com.kross.assignment3_kross;

public class Stock {
    String symbol;
    String companyName;
    Double latestPrice;
    Double change;
    Double changePercent;

    public Stock(String _symbol, String _companyName, Double _latestPrice, Double _change, Double _changePercent) {
        symbol = _symbol;
        companyName = _companyName;
        latestPrice = _latestPrice != null ? 0 : _latestPrice;
        change = _change != null ? 0 : _change;
        changePercent = _changePercent != null ? 0 : _changePercent;
    }
}
