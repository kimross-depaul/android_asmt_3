package com.kross.assignment3_kross.workers;

import android.app.Activity;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import com.kross.assignment3_kross.R;
import com.kross.assignment3_kross.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class DialogWorker {

    public static void list(Activity activity, String searchString, CompletionHandler completion) {
        NetworkWorker worker = new NetworkWorker(KeyWorker.getTickerUrl(), (result) -> {
            if (result != null) {
                try {
                    JSONArray jary = new JSONArray(result);
                    ArrayList<String> tempArray = new ArrayList<String>();
                    Boolean foundOne = false;

                    //FILTER THE JSON RESPONSE (LIST OF TICKERS) PER YOUR SEARCH
                    for (int i = 0; i < jary.length(); i++) {
                        JSONObject obj = (JSONObject) jary.getJSONObject(i);
                        if (obj.getString("symbol").contains(searchString)) {
                            tempArray.add(obj.getString("symbol") + " - " + obj.getString("name"));
                            //sArray[i] = obj.getString("symbol") + " - " + obj.getString("name");
                            foundOne = true;
                        }
                    }
                    String[] sArray = new String[tempArray.size()];
                    sArray = tempArray.toArray(sArray);

                    //REPORT IF NOTHING MATCHES YOUR SEARCH
                    if (!foundOne) {
                        AlertWorker.info(activity, "Symbol Not Found:  " + searchString, "Data for stock symbol", null);
                        completion.getResult("");
                        return;
                    }
                    //ASK THE USER WHICH ONE (IF WE GOT MULTIPLE RESULTS)
                    if (sArray.length > 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Make a selection");

                        //SET THE ITEMS FOR THE USER TO CHOOSE FROM
                        String[] finalSArray = sArray;
                        builder.setItems(sArray, (dialog, which) -> {
                            Log.d("DialogWorker", "--setting items closure");
                            try {
                                JSONObject obj = (JSONObject) jary.getJSONObject(which);
                                //PARSE THE TICKER/COMPANY RESULT INTO 2 PARTS
                                String choice = finalSArray[which];
                                completion.getResult(choice.split(" -")[0]);
                            } catch (JSONException rjex) {
                                Log.d("DialogWorker", "--A json parsing error occurred: " + rjex.getMessage());
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
                    //IF ONLY ONE RESULT WAS FOUND, JUST RETURN IT
                    } else {
                        completion.getResult(searchString);
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
}
