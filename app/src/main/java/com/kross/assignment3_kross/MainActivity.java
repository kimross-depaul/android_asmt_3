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

import com.kross.assignment3_kross.workers.DialogWorker;
import com.kross.assignment3_kross.workers.JsonWorker;
import com.kross.assignment3_kross.workers.KeyWorker;
import com.kross.assignment3_kross.workers.NetworkWorker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener  {
    private final ArrayList<Stock> stocks = new ArrayList<Stock>();
    private RecyclerView recyclerView;
    private StockAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JsonWorker.load(this, stocks);
        recyclerView = findViewById(R.id.vwStocks);
        adapter = new StockAdapter(stocks, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                refreshStocks();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        JsonWorker.save(stocks, this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    private void addTicker() {
        DialogWorker.list(this, (choice) -> {
            Log.d("MainActivity", "--Got the result " + choice);
            //Do other network call
            NetworkWorker worker = new NetworkWorker(KeyWorker.getStockUrl(choice), (result) -> {
                addStock(result);
            });
            new Thread(worker).start();
        });
    }

    private boolean stockExists(String symbol) {
        for (Stock stock: stocks) {
            if (stock.symbol.equals(symbol)){
                return true;
            }
        }
        return false;
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
            stocks.add(stock);
            runOnUiThread(() -> {
                Log.d("MainActivity", "--Refreshing the adapter in place " + (stocks.size()-1));
                adapter.notifyItemInserted(stocks.size() - 1);
                adapter.notifyDataSetChanged();
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshStocks() {
        StringBuilder sb = new StringBuilder();
        for (Stock stock: stocks) {
            sb.append(stock.symbol + ",");
        }
        Log.d("MainActivity", KeyWorker.getStockBatchUrl(sb.toString()));
        //The above line has the batch-stock url, need to parse it... knowing it's a dictionary
        /*
            {
                "A": {
                    "quote": {
                        "avgTotalVolume": 1329597,
                        "calculationPrice": "close",
                        "change": 0.5,
                        "changePercent": 0.00322,
                        "close": 155.76,
                        ..
                    }
                },
                "AA": {
                    "quote": {
                        "avgTotalVolume": 8654489,
                        "calculationPrice": "close",
                        "change": 0.66,
                        "changePercent": 0.01441,
                        "close": 46.45,
                        ... etc.
                     */
        swipeRefresh.setRefreshing(false);
    }

    // ------------------ MENU ITEMS ---------------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        addTicker();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}