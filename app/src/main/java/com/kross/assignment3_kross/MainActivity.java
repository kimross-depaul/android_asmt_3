package com.kross.assignment3_kross;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kross.assignment3_kross.workers.DialogWorker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    private void addTicker() {
        DialogWorker.list(this);
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