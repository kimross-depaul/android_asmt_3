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

public class StockDownloader implements Runnable {
    public final StockCollection stocks = new StockCollection();
    private MainActivity activity;

    public StockDownloader(MainActivity _activity) {
        this.activity = _activity;
    }

    @Override
    public void run() {
        refreshStocks();
    }

    // REFRESH THE EXISTING TICKERS WITH STOCK INFO
    public void refreshStocks() {
        String delimitedStocks = stocks.getDelimitedSymbols();
        if (delimitedStocks == "")
            return;

        //  RATHER THAN HIT EACH ENDPOINT, THIS PULLS A BATCH FROM A DIFFERENT ENDPOINT
        NetworkWorker worker = new NetworkWorker(KeyWorker.getStockBatchUrl(delimitedStocks), (result) -> {
            if (result != null && result != "!") {
                refreshEachStock(result);
            }else {
                AlertWorker.info( activity,"No Network Connection", "Stocks Cannot Be Updated Without a Network Connection" , null);
            }
            activity.swipeRefresh.setRefreshing(false);
        });

        new Thread(worker).start();
    }

    // THIS REFRESHES EACH ONE
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
            AlertWorker.info(activity, "Uh oh!", "Something happened:  " + jex.getMessage(), null);
        }
    }
}
