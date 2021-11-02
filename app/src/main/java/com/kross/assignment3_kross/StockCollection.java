package com.kross.assignment3_kross;

import androidx.annotation.NonNull;

import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;


public class StockCollection {
    private final HashMap<String, Stock> stocks;
    private String[] keyOrder = new String[0];
    private boolean needsReorder = false;
    private String[] cache;
/*
    public StockCollection(HashMap<String, Stock> stockList) {
        stocks = stockList;
    }*/

    // CONSTRUCTOR
    public StockCollection() {
        stocks = new HashMap<String, Stock>();
    }

    // SORTING
    public void reOrder() {
        keyOrder = stocks.keySet().toArray(new String[0]);
        Arrays.sort(keyOrder);
        needsReorder = false;
    }
    public void setNeedsReorder() {
        needsReorder = true;
    }
    public void reOrderIfNecessary() {
        if (needsReorder) {
            reOrder();
        }
    }

    // FINDING ELEMENTS BY ID OR KEY
    public Stock getByIndex(int position) {
        return stocks.get(keyOrder[position]);
    }
    public Stock getByKey(String key){
        return stocks.get(key);
    }
    public boolean containsKey(String key) {
        return stocks.containsKey(key);
    }

    // CRUD OPERATIONS
    public void remove(int position) {
        String key = keyOrder[position];
        stocks.remove(key);
        reOrder();
    }
    public void put(Stock stock, boolean shouldReorder) {
        stocks.put(stock.symbol, stock);
        if (shouldReorder) reOrder();
    }
    public void put(Stock stock) {
        put(stock, true);
    }
    public void clear() {
        stocks.clear();
        reOrder();
    }

    // CONVENIENCE FOR BATCH PROCESSING ENDPOINT
    public String getDelimitedSymbols() {
        StringBuilder sb = new StringBuilder();
        for (String symbol: stocks.keySet()) {
            sb.append(symbol + ",");
        }
        return sb.toString();
    }

    // RETURNS
    public Set<String> keys() {
        return stocks.keySet();
    }
    public String[] keyArray() {
        if (cache == null) {
            ArrayList<String> temp = new ArrayList<String>();
            for (String key : keyOrder) {
                temp.add(key + " - " + getByKey(key).companyName);
            }
            cache = temp.toArray(new String[0]);
        }
        return cache;
    }
    public int size() {
        return stocks.size();
    }

    // FOR JSON WRITING
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
                jsonWriter.name("companyName").value(getByKey(key).companyName);
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
