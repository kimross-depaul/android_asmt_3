package com.kross.assignment3_kross;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kross.assignment3_kross.workers.DialogWorker;
import com.kross.assignment3_kross.workers.JsonWorker;
import com.kross.assignment3_kross.workers.KeyWorker;
import com.kross.assignment3_kross.workers.NetworkWorker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener  {
    private final ArrayList<Stock> stocks = new ArrayList<Stock>();
    private RecyclerView recyclerView;
    private StockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JsonWorker.load(this, stocks);
        recyclerView = findViewById(R.id.vwStocks);
        adapter = new StockAdapter(stocks, this);
        recyclerView.setAdapter(adapter);
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
                //What we do when we retrieve this stock's details
                Log.d("MainActivity", "--" + result);
            });
            new Thread(worker).start();
        });
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