package com.kross.assignment3_kross;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.StringWriter;

public class Stock {
    public String symbol;
    public String companyName;
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
        companyName = "";
        latestPrice = 0.0;
        change = 0.0;
        changePercent = 0.0;
    }
    public Stock(String _symbol, String _companyName) {
        this.symbol = _symbol;
        this.companyName = _companyName;
    }
}
