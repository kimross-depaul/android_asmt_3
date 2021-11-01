package com.kross.assignment3_kross;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.kross.assignment3_kross.workers.DialogWorker;
import com.kross.assignment3_kross.workers.JsonWorker;
import com.kross.assignment3_kross.workers.KeyWorker;
import com.kross.assignment3_kross.workers.NetworkWorker;
import com.kross.assignment3_kross.workers.AlertWorker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener  {
   // private final HashMap<String, Stock> stocks = new HashMap<String, Stock>();
    private final StockCollection stocks = new StockCollection();
    private RecyclerView recyclerView;
    private StockAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JsonWorker.load(this, stocks);

        Log.d("MainActivity", "stocks loaded from json = " + stocks.size());
        recyclerView = findViewById(R.id.vwStocks);
        adapter = new StockAdapter(stocks, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setScrollbarFadingEnabled(false);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                refreshStocks();
            }
        });
       refreshStocks();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JsonWorker.save(stocks, this); //TODO
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock stock = stocks.getByIndex(pos);
        AlertWorker.okToDelete(MainActivity.this, "Delete Stock", "Delete Stock Symbol " + stock.symbol + "?", (dialog, id) -> {
            stocks.remove(pos);
            adapter.notifyItemRemoved(pos);
        }, (dialog, id) -> {
            //Cancelled - just return
        });
        return false;
    }

    private void addTicker(String searchString) {
        DialogWorker.list(this, searchString, (choice) -> {
            Log.d("MainActivity", "--Got the result " + choice);
            //Do other network call

            Log.d("MainActivity", "-- getting details for choice");
            NetworkWorker worker = new NetworkWorker(KeyWorker.getStockUrl(choice), (result) -> {
                if (result != null && result != "") {
                    addStock(result);
                }else {
                    AlertWorker.info( MainActivity.this, "No Network Connection", "Stocks Cannot Be Updated Without a Network Connection" );
                }
            });
            new Thread(worker).start();
        });
    }

    private boolean stockExists(String symbol) {
        return stocks.containsKey(symbol);
    }

    private void addStock(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            if (stockExists(obj.getString("symbol"))) {
                Log.d("MainActivity", "--This stock already exists");// TODO
                return;
            }
            createTableRow(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createTableRow(JSONObject obj) {
        try {
            String symbol = obj.getString("symbol");
            String companyName = obj.getString("companyName");
            Double latestPrice = obj.getDouble("latestPrice");
            Double change = obj.getDouble("change");
            Double changePercent = obj.getDouble("changePercent");
            Stock stock = new Stock(symbol, companyName, latestPrice, change, changePercent);
            stocks.put(stock);
            runOnUiThread(() -> {
                Log.d("MainActivity", "--Refreshing the adapter in place " + (stocks.size()-1));
                //adapter.notifyItemInserted(stocks.size() - 1);
                adapter.setNeedsReorder();
                adapter.notifyDataSetChanged();
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshStocks() {
        String delimitedStocks = stocks.getDelimitedSymbols();
        Log.d("MainActivity", KeyWorker.getStockBatchUrl(delimitedStocks));
        if (delimitedStocks == "")
            return;
        NetworkWorker worker = new NetworkWorker(KeyWorker.getStockBatchUrl(delimitedStocks), (result) -> {
            if (result != null) {
                refreshEachStock(result);
            }else {
                AlertWorker.info( MainActivity.this,"No Network Connection", "Stocks Cannot Be Updated Without a Network Connection" );
            }
            swipeRefresh.setRefreshing(false);
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
            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
            });
        } catch (JSONException jex) {
            Log.d("MainActivity", "-- Couldn't read batch:  " + jex.getMessage());
        }
    }
    // ------------------ MENU ITEMS ---------------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //TODO
        AlertWorker.input(MainActivity.this, "StockSelection", "Please enter a Stock Symbol:", (result) -> {
            Log.d("MainActivity", result);
            addTicker(result);
        });

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}