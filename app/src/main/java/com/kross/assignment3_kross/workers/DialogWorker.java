package com.kross.assignment3_kross.workers;

import android.app.Activity;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import com.kross.assignment3_kross.R;
import com.kross.assignment3_kross.MainActivity;
import com.kross.assignment3_kross.Stock;
import com.kross.assignment3_kross.StockCollection;
import com.kross.assignment3_kross.workers.runners.NameDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DialogWorker {
    private StockCollection stocks;
    private Activity activity;

    public DialogWorker(Activity _activity) {
        this.activity = _activity;
        stocks = new StockCollection();
    }

    public void list(String searchString, CompletionHandler completion) {
        if (stocks.size() == 0) {
            launchDialog(searchString, completion);
        }else {
            launchCachedDialog(searchString, completion);
        }
    }

    private void launchDialog(String searchString, CompletionHandler completion) {
        NetworkWorker worker = new NetworkWorker(KeyWorker.getTickerUrl(), (result) -> {
            if (result != null) {
                try {
                    JSONArray jary = new JSONArray(result);
                    convertJaryToHash(jary);
                    String[] sArray = filter(searchString);

                    //REPORT IF NOTHING MATCHES YOUR SEARCH
                    if (sArray.length == 0) {
                        AlertWorker.info(activity, "Symbol Not Found:  " + searchString, "Data for stock symbol", null);
                        completion.getResult("");
                        return;
                    }
                    if (sArray.length == 1) {
                        completion.getResult(searchString);
                    }else {
                        populateChoices(sArray, searchString, completion);
                    }

                } catch (JSONException jex) {
                    Log.d("DialogWorker", "--A json parsing error occurred: " + jex.getMessage());
                }
            } else {
                AlertWorker.info( activity,"No Network Connection", "Stocks Cannot Be Updated Without a Network Connection" , null);
            }
        });

        new Thread(worker).start();
    }
    private void convertJaryToHash(JSONArray jary) {
        for(int i = 0 ; i < jary.length(); i++) {
            JSONObject obj = null;
            try {
                obj = (JSONObject) jary.getJSONObject(i);
                String symbol = obj.getString("symbol");
                String name = obj.getString("name");
                stocks.put(new Stock(symbol, name), false);
                //Log.d("DialogWorker", "Just put symbol " + symbol + " to the list");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        stocks.reOrder();
    }

    private String[] filter( String searchString) {
        //FILTER THE JSON RESPONSE (LIST OF TICKERS) PER YOUR SEARCH
        ArrayList<String> tempArray = new ArrayList<String>();
        String[] temp = stocks.keyArray();
        temp = Arrays.stream(temp).filter(s -> s.startsWith(searchString)).toArray(String[]::new);
        return temp;
/*
        Log.d("DialogWorker", "-------temp[0]" + temp[0]);

        for (String symbol: stocks.keys()) {
            ///Log.d("DialogWorker", "searching for " + searchString + " in " + symbol);
            if (symbol.startsWith(searchString)){
                tempArray.add(symbol + " - " + stocks.getByKey(symbol).companyName);
            }
        }

        String[] sArray = new String[tempArray.size()];
        sArray = tempArray.toArray(sArray);
        return sArray;*/
    }
    private void launchCachedDialog(String searchString, CompletionHandler completion) {
        Log.d("DialogWorker", "--BEFORE populating cached choices: " + stocks.keyArray());
        Log.d("DialogWorker", "--stocks[0].name" + stocks.getByIndex(0).companyName);
        String[] sArray = filter(searchString);
        populateChoices(sArray, searchString, completion);
    }

    private void populateChoices(String[] sArray, String searchString, CompletionHandler completion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Make a selection");

        //SET THE ITEMS FOR THE USER TO CHOOSE FROM
        String[] finalSArray = sArray;
        builder.setItems(sArray, (dialog, which) -> {
            Log.d("DialogWorker", "--setting items closure");
            try {
                String choice = finalSArray[which];
                completion.getResult(choice.split(" -")[0]);
            } catch (Exception ex) {
                Log.d("DialogWorker", "--An unexpected error occurred: " + ex.getMessage());
            }
        });

        builder.setNegativeButton("Nevermind", (dialog, id) -> {
        });

        activity.runOnUiThread(() -> {
            Log.d("DialogWorker", "--creating dialog to show");
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.d("DialogWorker", "--showing the dialog");
        });
    }
}
