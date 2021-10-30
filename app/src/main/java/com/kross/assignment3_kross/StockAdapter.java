package com.kross.assignment3_kross;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {
    private final MainActivity mainAct;
    private final List<Stock> stocks;

    StockAdapter(List<Stock> _stocks, MainActivity _mainAct) {
        this.stocks = _stocks;
        mainAct = _mainAct;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View stockView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_view_row, parent, false);
        stockView.setOnClickListener(mainAct);
        stockView.setOnLongClickListener(mainAct);

        return new StockViewHolder(stockView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stocks.get(position);
        holder.lblSymbol.setText(stock.symbol);
        holder.lblCompanyName.setText(stock.companyName);
        holder.lblLatestPrice.setText(String.format("%.2f", stock.latestPrice));
        holder.lblChange.setText(arrow(stock.change) + String.format("%.2f (%.2f", stock.change, stock.changePercent) + "%)");
        colorFields(stock.change, holder);
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
