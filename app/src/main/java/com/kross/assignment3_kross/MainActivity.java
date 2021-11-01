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
import com.kross.assignment3_kross.workers.runners.NameDownloader;
import com.kross.assignment3_kross.workers.runners.StockDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener  {
   // private final HashMap<String, Stock> stocks = new HashMap<String, Stock>();
    private DialogWorker dialogWorker;
    private StockDownloader stockDownloader;
    public RecyclerView recyclerView;

    public StockAdapter adapter;
    public SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stockDownloader = new StockDownloader(this);
        dialogWorker = new DialogWorker(this);
        JsonWorker.load(this, stockDownloader.stocks);

        Log.d("MainActivity", "stocks loaded from json = " + stockDownloader.stocks.size());
        recyclerView = findViewById(R.id.vwStocks);
        adapter = new StockAdapter(stockDownloader.stocks, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setScrollbarFadingEnabled(false);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                stockDownloader.refreshStocks();
            }
        });
       stockDownloader.refreshStocks();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JsonWorker.save(stockDownloader.stocks, this); //TODO
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock stock = stockDownloader.stocks.getByIndex(pos);
        AlertWorker.okToDelete(MainActivity.this, "Delete Stock", "Delete Stock Symbol " + stock.symbol + "?", (dialog, id) -> {
            stockDownloader.stocks.remove(pos);
            adapter.notifyItemRemoved(pos);
        }, (dialog, id) -> {
            //Cancelled - just return
        });
        return false;
    }

    private void addTicker(String searchString) {
        dialogWorker.list(searchString, (choice) -> {
            Log.d("MainActivity", "--Got the result " + choice);
            NetworkWorker worker = new NetworkWorker(KeyWorker.getStockUrl(choice), (result) -> {
                if (result != null && result != "") {
                    addStock(result);
                } else if (result == "!") {
                    AlertWorker.info( MainActivity.this, "No Network Connection", "Stocks Cannot Be Updated Without a Network Connection" , null);
                } else {
                    Log.d("MainActivity", "-- Could not add this ticker.");
                }
            });
            new Thread(worker).start();
        });
    }

    private boolean stockExists(String symbol) {
        return stockDownloader.stocks.containsKey(symbol);
    }

    private void addStock(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String thisSymbol = obj.getString("symbol");
            if (stockExists(thisSymbol)) {
                AlertWorker.info(MainActivity.this, "Duplicate Stock", "Stock Symbol " + thisSymbol + " is already displayed", R.drawable.ic_baseline_warning_24);
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
            stockDownloader.stocks.put(stock);
            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    // ------------------ MENU ITEMS ---------------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //TODO
        AlertWorker.input(MainActivity.this, "Stock Selection", "Please enter a Stock Symbol:", (result) -> {
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