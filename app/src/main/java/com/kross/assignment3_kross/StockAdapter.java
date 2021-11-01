package com.kross.assignment3_kross;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {
    private final MainActivity mainAct;
    private final StockCollection stocks;

    StockAdapter(StockCollection _stocks, MainActivity _mainAct) {
        this.stocks = _stocks;
        stocks.reOrder();
        mainAct = _mainAct;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        stocks.reOrderIfNecessary();
        View stockView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_view_row, parent, false);
        stockView.setOnClickListener(mainAct);
        stockView.setOnLongClickListener(mainAct);

        return new StockViewHolder(stockView);
    }

    public void setNeedsReorder() {
        stocks.setNeedsReorder();
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stocks.getByIndex(position);
        holder.lblSymbol.setText(stock.symbol);
        holder.lblCompanyName.setText(stock.companyName);
        holder.lblLatestPrice.setText(String.format("%.2f", stock.latestPrice != null ? stock.latestPrice : 0));
        Double change = stock.change != null ? stock.change : 0;
        holder.lblChange.setText(arrow(change) + String.format("%.2f (%.2f",
                change,
                stock.changePercent != null ? stock.changePercent : 0
        ) + "%)");
        colorFields(change, holder);
    }

    private String arrow(Double change) {
        return change < 0 ? "▼" : "▲";
    }
    private void colorFields(Double change, StockViewHolder holder) {
        int color = change < 0 ? Color.RED : Color.GREEN;

        holder.lblSymbol.setTextColor(color);
        holder.lblCompanyName.setTextColor(color);
        holder.lblLatestPrice.setTextColor(color);
        holder.lblChange.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }
}
