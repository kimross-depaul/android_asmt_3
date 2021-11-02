package com.kross.assignment3_kross.workers.runners;

import android.app.Activity;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.kross.assignment3_kross.Stock;
import com.kross.assignment3_kross.StockCollection;
import com.kross.assignment3_kross.workers.AlertWorker;
import com.kross.assignment3_kross.workers.CompletionHandler;
import com.kross.assignment3_kross.workers.KeyWorker;
import com.kross.assignment3_kross.workers.NetworkWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;

public class NameDownloader {
    private StockCollection stocks;
    private Activity activity;

    public NameDownloader(Activity _activity) {
        this.activity = _activity;
        stocks = new StockCollection();
    }

    // LAUNCH EITHER THE DOWNLOADED OR CACHED LIST OF TICKERS
    public void list(String searchString, CompletionHandler completion) {
        if (stocks.size() == 0) {
            launchDialog(searchString, completion);
        }else {
            launchCachedDialog(searchString, completion);
        }
    }

    // FIRST-TIME LOADING DIALOG
    private void launchDialog(String searchString, CompletionHandler completion) {
        NetworkWorker worker = new NetworkWorker(KeyWorker.getTickerUrl(), (result) -> {
            if (result != null && result != "!") {
                try {
                    JSONArray jary = new JSONArray(result);
                    convertJaryToHash(jary);
                    String[] sArray = filter(searchString);

                    handleFilteredChoices(sArray, searchString, completion);

                } catch (JSONException jex) {
                    AlertWorker.info(activity, "Uh oh!", "Something happened:  " + jex.getMessage(), null);
                }
            } else {
                AlertWorker.info( activity,"No Network Connection", "Stocks Cannot Be Updated Without a Network Connection" , null);
            }
        });

        new Thread(worker).start();
    }

    // SUBSEQUENT LOADS - PULL CACHED RESULTS
    private void launchCachedDialog(String searchString, CompletionHandler completion) {
        String[] sArray = filter(searchString);
        handleFilteredChoices(sArray, searchString, completion);
    }

    // -----------------------------------------------------
    // --------------- CONVENIENCE METHODS -----------------
    // -----------------------------------------------------
    private void convertJaryToHash(JSONArray jary) {
        for(int i = 0 ; i < jary.length(); i++) {
            JSONObject obj = null;
            try {
                obj = (JSONObject) jary.getJSONObject(i);
                String symbol = obj.getString("symbol");
                String name = obj.getString("name");
                stocks.put(new Stock(symbol, name), false);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        stocks.reOrder();
    }
    private void handleFilteredChoices(String[] sArray, String searchString, CompletionHandler completion) {
        //REPORT IF NOTHING MATCHES YOUR SEARCH
        if (sArray.length == 0) {
            AlertWorker.info(activity, "Symbol Not Found:  " + searchString, "Data for stock symbol", null);
            completion.getResult("");
            return;
        }
        //RETURN YOUR SEARCH STRING IF ONLY ONE RESULT
        if (sArray.length == 1) {
            completion.getResult(sArray[0].split(" -")[0]);

        //ASK THE USER TO NARROW THE CHOICE
        }else {
            populateChoices(sArray, searchString, completion);
        }
    }

    // NARROW DOWN THE TICKER CHOICES
    private String[] filter( String searchString) {
        ArrayList<String> tempArray = new ArrayList<String>();
        String[] temp = stocks.keyArray();
        temp = Arrays.stream(temp).filter(s -> s.startsWith(searchString)).toArray(String[]::new);
        return temp;
    }

    // ADD THE TICKERS TO THE DIALOG CHOICES
    private void populateChoices(String[] sArray, String searchString, CompletionHandler completion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Make a selection");

        //SET THE ITEMS FOR THE USER TO CHOOSE FROM
        String[] finalSArray = sArray;
        builder.setItems(sArray, (dialog, which) -> {
            try {
                String choice = finalSArray[which];
                completion.getResult(choice.split(" -")[0]);
            } catch (Exception ex) {
                AlertWorker.info(activity, "Uh oh!", "Something happened:  " + ex.getMessage(), null);
            }
        });

        builder.setNegativeButton("Nevermind", (dialog, id) -> {
            // Just return
        });

        activity.runOnUiThread(() -> {
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}
