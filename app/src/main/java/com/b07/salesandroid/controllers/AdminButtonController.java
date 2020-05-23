package com.b07.salesandroid.controllers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Spinner;
import com.b07.salesandroid.R;
import com.b07.salesandroid.models.AdminModel;

/**
 * This button controller is for Admin commands.
 */
public class AdminButtonController implements View.OnClickListener {

    private Context appContext;

    public AdminButtonController(Context context) {
      appContext = context;
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()) {
        // PromoteEmployee
        case R.id.promoteEmployee:
          Spinner employeeSpinner = ((Activity) appContext).findViewById(R.id.employeeListSpinner);
          AdminModel.buildPromoteAlert(view.getContext(), employeeSpinner.getSelectedItem().toString());
          break;
      }
  }
}