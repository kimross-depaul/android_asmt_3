package com.kross.assignment3_kross.workers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import com.kross.assignment3_kross.MainActivity;
import com.kross.assignment3_kross.Stock;
import com.kross.assignment3_kross.StockCollection;

public class JsonWorker {
    private final static String FILENAME = "Stocks.json";

    // READ THE JSON FILE
    public static void load(MainActivity activity, StockCollection stockCol) {
        try {
            //activity.getApplicationContext().deleteFile(FILENAME);
            InputStream is = activity.getApplicationContext().openFileInput(FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            String line;
            StringBuilder sb = new StringBuilder("");
            while ((line = reader.readLine()) != null) {
                Log.d("JsonWorker", "--" + line);
                sb.append(line);
            }
            JSONArray jsonArray = new JSONArray(sb.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String symbol = jsonObject.getString("symbol");
                String name = jsonObject.getString("companyName");
                Stock newStock = new Stock(symbol, name);
                stockCol.put(newStock);
            }

            if (activity.swipeRefresh != null)
                activity.swipeRefresh.setRefreshing(false);
        } catch(Exception ex){
            Log.d("JsonWorker", "No existing stocks were saved");
        }
    }

    // WRITE TO THE JSON FILE
    public static void save(StockCollection stockCol, Activity activity) {
        try {
            FileOutputStream fos = activity.getApplicationContext().
                    openFileOutput(FILENAME, Context.MODE_PRIVATE);

            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(stockCol.toString());
            Log.d("JsonWorker", stockCol.toString());
            printWriter.close();
            fos.close();
        }catch (Exception ex){
            AlertWorker.info(activity, "Uh oh!", "Something happened:  " + ex.getMessage(), null);
        }
    }
}


