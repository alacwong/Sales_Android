package com.b07.salesandroid.models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.b07.salesandroid.views.PromoteEmployeeView;

public class AdminModel {
  public static void buildPromoteAlert (Context context, String employee) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    if (!(employee).isEmpty()) {
      String message = "Are you sure you want to promote " + employee + "?";
      builder.setMessage(message)
          .setCancelable(false)
          .setPositiveButton("Yes", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  // promote
                  // need view from PromoteEmployeeView here but cant get it cause it's in a
                  // class/function without a view.
                  // can't use attachBaseContext since it modifies the entire class's context(?)
                  // Toast.makeText(getApplicationContext(), "Promoted!", Toast.LENGTH_SHORT).show();
                }
              }
          )
          .setNegativeButton("No", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.cancel();
                }
              }
          );
      // don't need to do builder.create().show() since we aren't accessing the builder later.
      builder.show();
    } else {
      builder.setMessage("No employee selected.")
          .setNeutralButton("OK", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.cancel();
                }
              }
          );
    }
  }
}
