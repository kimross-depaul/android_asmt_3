package com.kross.assignment3_kross;

import androidx.annotation.NonNull;

public class Stock {
    String symbol;
    String companyName;
    Double latestPrice;
    Double change;
    Double changePercent;

    public Stock(String _symbol, String _companyName, Double _latestPrice, Double _change, Double _changePercent) {
        symbol = _symbol;
        companyName = _companyName;
        latestPrice = _latestPrice != null ? _latestPrice : 0;
        change = _change != null ? _change: 0;
        changePercent = _changePercent != null ? _changePercent : 0;
    }
    public Stock(String _symbol) {
        symbol = _symbol;
    }

    @NonNull
    @Override
    public String toString() {
        return symbol;
    }
}
