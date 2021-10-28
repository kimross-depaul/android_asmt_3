package com.kross.assignment3_kross.workers;

public class KeyWorker {
    private static final String TICKER_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private static final String STOCK_URL_BASE = "https://cloud.iexapis.com/stable/stock/";
    private static final String STOCK_URL_SUFFIX = "/quote?token=";
    private static final String STOCK_API_KEY = "pk_06c54217ded5495a911f9230cd4e79ec";

    public static String getTickerUrl() {
        return TICKER_URL;
    }

    public static String getStockUrl(String symbol) {
        return STOCK_URL_BASE + symbol + STOCK_URL_SUFFIX + STOCK_API_KEY;
    }
}
