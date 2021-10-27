package com.kross.assignment3_kross.workers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class DialogWorker {

    public static void list(Activity activity, DialogCompletion completion) {
        final CharSequence[] sArray = new CharSequence[20];

        NetworkWorker worker = new NetworkWorker((result) -> {
            Log.d("DialogWorker", "--" + result);
            for (int i = 0; i < 20; i++)
                sArray[i] = "Choice " + i;

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Make a selection");

            builder.setItems(sArray, (dialog, which) -> {
                completion.getChoice(sArray[which].toString());
            });

            builder.setNegativeButton("Nevermind", (dialog, id) -> {});

            AlertDialog dialog = builder.create();
            dialog.show();
        });


    }
}
