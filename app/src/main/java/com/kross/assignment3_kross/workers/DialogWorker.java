package com.kross.assignment3_kross.workers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DialogWorker {

    public static void list(Activity activity, DialogCompletion completion) {
        NetworkWorker worker = new NetworkWorker((result) -> {
            try {
                JSONArray jary = new JSONArray(result);
                CharSequence[] sArray = new CharSequence[jary.length()];

                for (int i = 0; i < jary.length(); i++){
                    JSONObject obj = (JSONObject)jary.getJSONObject(i);
                    sArray[i] = obj.getString("symbol") + " - " + obj.getString("name");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Make a selection");

                builder.setItems(sArray, (dialog, which) -> {
                    try {
                        JSONObject obj = (JSONObject) jary.getJSONObject(which);
                        completion.getChoice(obj.getString("symbol")); //sArray[which].toString());
                    } catch (JSONException rjex) {
                        Log.d("DialogWorker", "--A json parsing error occurred: " + rjex.getMessage());
                    }
                });

                builder.setNegativeButton("Nevermind", (dialog, id) -> {
                });

                activity.runOnUiThread(() -> {
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
            } catch (JSONException jex){
                Log.d("DialogWorker", "--A json parsing error occurred: " + jex.getMessage());
            }
        });

        new Thread(worker).start();
    }
}
