package com.kross.assignment3_kross;

import androidx.annotation.NonNull;

import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;


public class StockCollection {
    private final HashMap<String, Stock> stocks;
    private String[] keyOrder = new String[0];
    private boolean needsReorder = false;

    public StockCollection(HashMap<String, Stock> stockList) {
        stocks = stockList;
    }
    public StockCollection() {
        stocks = new HashMap<String, Stock>();
    }
    public void reOrder() {
        keyOrder = stocks.keySet().toArray(new String[0]);
        Arrays.sort(keyOrder);
        needsReorder = false;
    }
    public Stock getByIndex(int position) {
        return stocks.get(keyOrder[position]);
    }
    public Stock getByKey(String key){
        return stocks.get(key);
    }
    public void remove(int position) {
        String key = keyOrder[position];
        stocks.remove(key);
        reOrder();
    }
    public String getDelimitedSymbols() {
        StringBuilder sb = new StringBuilder();
        for (String symbol: stocks.keySet()) {
            sb.append(symbol + ",");
        }
        return sb.toString();
    }
    public void put(Stock stock) {
        stocks.put(stock.symbol, stock);
        reOrder();
    }
    public Set<String> keys() {
        return stocks.keySet();
    }
    public boolean containsKey(String key) {
        return stocks.containsKey(key);
    }
    public int size() {
        return stocks.size();
    }

    public void setNeedsReorder() {
        needsReorder = true;
    }

    public void reOrderIfNecessary() {
        if (needsReorder) {
            reOrder();
        }
    }

    @NonNull
    @Override
    public String toString() {
        try {
            StringWriter writer = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.beginArray();
            for (String key: keyOrder) {
                jsonWriter.beginObject();
                jsonWriter.name("symbol").value(key);
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.close();
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
