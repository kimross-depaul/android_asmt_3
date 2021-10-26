package com.kross.assignment3_kross.workers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class DialogWorker {


    public static void list(Activity activity) {
        final CharSequence[] sArray = new CharSequence[20];
        for (int i = 0; i < 20; i++)
            sArray[i] = "Choice " + i;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Make a selection");

        // Set the builder to display the string array as a selectable
        // list, and add the "onClick" for when a selection is made
        builder.setItems(sArray, (dialog, which) -> Log.d("DialogWorker", sArray[which] + ""));

        builder.setNegativeButton("Nevermind", (dialog, id) -> {
            //tv2.setText(getString(R.string.nevermind_selected));
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }
}
