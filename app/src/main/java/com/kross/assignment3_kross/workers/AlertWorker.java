package com.kross.assignment3_kross.workers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.kross.assignment3_kross.R;

public class AlertWorker {

    public static void okCancel(Activity activity, String title, String message,
                                DialogInterface.OnClickListener okBehavior,
                                DialogInterface.OnClickListener cancelBehavior,
                                String okButtonTitle, String cancelButtonTitle, Integer icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(okButtonTitle, okBehavior);
        if (cancelBehavior != null)
            builder.setNegativeButton(cancelButtonTitle, cancelBehavior);

        if (icon != null)
            builder.setIcon(icon);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void okToDelete(Activity activity, String title, String message,
                                  DialogInterface.OnClickListener okBehavior,
                                  DialogInterface.OnClickListener cancelBehavior) {
        okCancel(activity, title, message, okBehavior, cancelBehavior, "DELETE", "CANCEL", R.drawable.ic_baseline_delete_24);
    }
    public static void ok(Activity activity, String title, String message,
                          DialogInterface.OnClickListener okBehavior) {
        okCancel(activity, title, message, okBehavior, null, "OK", "", null);
    }

    public static void input(Activity activity, String title, String message,
                             DialogInterface.OnClickListener okBehavior,
                             DialogInterface.OnClickListener cancelBehavior) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final EditText et = new EditText(activity);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setId(R.id.inputSymbol);

        builder.setView(et);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", okBehavior);
        if (cancelBehavior != null)
            builder.setNegativeButton("Cancel", cancelBehavior);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
