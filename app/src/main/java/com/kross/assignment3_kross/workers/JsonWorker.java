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

import com.kross.assignment3_kross.Stock;
import com.kross.assignment3_kross.StockCollection;

public class JsonWorker {
    private final static String FILENAME = "Stocks.json";

    public static void load(Activity activity, StockCollection stockCol) {
        try {
            //activity.getApplicationContext().deleteFile(FILENAME);
            InputStream is = activity.getApplicationContext().openFileInput(FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            String line;
            StringBuilder sb = new StringBuilder("");
            while ((line = reader.readLine()) != null) {
                Log.d("JsonWorker", "-- line:" + line);
                sb.append(line);
            }
            JSONArray jsonArray = new JSONArray(sb.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String symbol = jsonObject.getString("symbol");
                Stock newStock = new Stock(symbol);
                stockCol.put(newStock);
            }
        } catch(Exception ex){
            Log.d("NoteWorker", "--Unable to load json file " + FILENAME + ":  " + ex.getMessage());
        }
    }
    public static void save(StockCollection stockCol, Activity activity) {
        try {
            Log.d("JsonWorker", "--SAVING TO DISK");
            FileOutputStream fos = activity.getApplicationContext().
                    openFileOutput(FILENAME, Context.MODE_PRIVATE);

            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(stockCol.toString());
            Log.d("JsonWorker", stockCol.toString());
            printWriter.close();
            fos.close();
        }catch (Exception ex){
            Log.d("NoteWorker", "--Unable to save json file " + FILENAME + ":  " + ex.getMessage());
        }
    }
}


