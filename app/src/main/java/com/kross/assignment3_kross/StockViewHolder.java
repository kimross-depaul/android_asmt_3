package com.kross.assignment3_kross;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder {
    TextView lblSymbol;
    TextView lblCompanyName;
    TextView lblLatestPrice;
    TextView lblChange;

    StockViewHolder(View view) {
        super(view);
        lblSymbol = view.findViewById(R.id.lblSymbol);
        lblCompanyName = view.findViewById(R.id.lblCompanyName);
        lblLatestPrice = view.findViewById(R.id.lblLatestPrice);
        lblChange = view.findViewById(R.id.lblChange);
    }
}
