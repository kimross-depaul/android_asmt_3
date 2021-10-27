package com.example.assignment2_kross.workers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertWorker {

    public static void okCancel(Activity activity, String title, String message,
                                DialogInterface.OnClickListener okBehavior,
                                DialogInterface.OnClickListener cancelBehavior) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", okBehavior);
        if (cancelBehavior != null)
            builder.setNegativeButton("Cancel", cancelBehavior);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void ok(Activity activity, String title, String message,
                          DialogInterface.OnClickListener okBehavior) {
        okCancel(activity, title, message, okBehavior, null);
    }
}
