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
                    Log.d("DialogWorker", "--This is the result array:  " + result);
                    Log.d("DialogWorker", "--the array's length is " + jary.length());

                    ArrayList<String> tempArray = new ArrayList<String>();
                    Boolean foundOne = false;

                    for (int i = 0; i < jary.length(); i++) {
                        JSONObject obj = (JSONObject) jary.getJSONObject(i);
                        if (obj.getString("symbol").contains(searchString)) {
                            tempArray.add(obj.getString("symbol") + " - " + obj.getString("name"));
                            //sArray[i] = obj.getString("symbol") + " - " + obj.getString("name");
                            foundOne = true;
                        }
                    }
                    String[] sArray = new String[tempArray.size()]; //(CharSequence[]) tempArray.toArray();//new CharSequence[tempArray.size()];
                    sArray = tempArray.toArray(sArray);

                    if (!foundOne) {
                        AlertWorker.info(activity, "Symbol Not Found:  " + searchString, "Data for stock symbol", null);
                        completion.getResult("");
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Make a selection");

                    String[] finalSArray = sArray;
                    builder.setItems(sArray, (dialog, which) -> {
                        Log.d("DialogWorker", "--setting items closure");
                        try {
                            JSONObject obj = (JSONObject) jary.getJSONObject(which);
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
