package com.kross.assignment3_kross.workers.runners;

import android.util.Log;

import com.kross.assignment3_kross.MainActivity;
import com.kross.assignment3_kross.Stock;
import com.kross.assignment3_kross.StockCollection;
import com.kross.assignment3_kross.workers.AlertWorker;
import com.kross.assignment3_kross.workers.KeyWorker;
import com.kross.assignment3_kross.workers.NetworkWorker;

import org.json.JSONException;
import org.json.JSONObject;

public class NameDownloader implements Runnable {
    public final StockCollection stocks = new StockCollection();
    private MainActivity activity;

    public NameDownloader(MainActivity _activity) {
        this.activity = _activity;
    }

    @Override
    public void run() {
        refreshStocks();
    }

    public void refreshStocks() {
        String delimitedStocks = stocks.getDelimitedSymbols();
        Log.d("MainActivity", KeyWorker.getStockBatchUrl(delimitedStocks));
        if (delimitedStocks == "")
            return;
        NetworkWorker worker = new NetworkWorker(KeyWorker.getStockBatchUrl(delimitedStocks), (result) -> {
            if (result != null) {
                refreshEachStock(result);
            }else {
                AlertWorker.info( activity,"No Network Connection", "Stocks Cannot Be Updated Without a Network Connection" , null);
            }
            activity.swipeRefresh.setRefreshing(false);
        });

        new Thread(worker).start();
    }
    private void refreshEachStock(String json) {
        try {
            JSONObject root = new JSONObject(json);

            for (String symbol: stocks.keys()) {
                Stock stock = stocks.getByKey(symbol);
                JSONObject thisStock = (JSONObject) root.get(stock.symbol);
                JSONObject quote = (JSONObject) thisStock.get("quote");
                Stock freshStock = new Stock(quote.getString("symbol"), quote.getString("companyName"), quote.getDouble("latestPrice"), quote.getDouble("change"), quote.getDouble("changePercent"));
                stocks.put(freshStock);
            }
            activity.runOnUiThread(() -> {
                activity.adapter.notifyDataSetChanged();
            });
        } catch (JSONException jex) {
            Log.d("MainActivity", "-- Couldn't read batch:  " + jex.getMessage());
        }
    }
}
