package com.b07.salesandroid.controllers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.exceptions.ExceptionHandler;
import com.b07.exceptions.UserNotFoundException;
import com.b07.salesandroid.R;
import com.b07.users.Customer;
import java.lang.reflect.Array;
import java.util.List;

public class AccountButtonController implements View.OnClickListener {

  private Context appContext;

  public AccountButtonController(Context context) {
    appContext = context;
  }
  @Override
  public void onClick(View view) {
      switch (view.getId()) {
      // PromoteEmployee
        case R.id.showAccount:
          DatabaseAndroidHelper db = new DatabaseAndroidHelper(appContext);
          Spinner selectedUser = ((Activity) appContext).findViewById(R.id.customerAccounts);
          Customer customer = null;
          if (selectedUser.getSelectedItem() != null){
            String parse = selectedUser.getSelectedItem().toString();

            try {
              int id = parse.charAt(0) - '0';
              customer = (Customer) db.getUserDetailsHelper(id, view.getContext());
            } catch (UserNotFoundException e){
              ExceptionHandler.handle(e);
            }
          } else {
            return;
          }
          Spinner option = ((Activity) appContext).findViewById(R.id.accountOption);
          String text = option.getSelectedItem().toString();
          String[] stringArray = appContext.getResources().getStringArray(R.array.accountOptions);

          ListView accountIds = ((Activity) appContext).findViewById(R.id.accountIds);

            ///TODO DOUBLE CHECK XML I COULD BE GETTING INCORRECT IDS
          if (text.equals(stringArray[0])){
            //TODO display this on the scoll view
            List<Integer> ids = db.getUserAccountIdsHelper(customer);
            ArrayAdapter adapter = new ArrayAdapter(appContext, android.R.layout.simple_list_item_1, ids);
            accountIds.setAdapter(adapter);
          } else if (text.equals(stringArray[1])){
            //TODO display this on the scoll view
            List<Integer> ids = db.getUserActiveAccountIdsHelper(customer);
            ArrayAdapter adapter = new ArrayAdapter(appContext, android.R.layout.simple_list_item_1, ids);
            accountIds.setAdapter(adapter);
          } else if (text.equals(stringArray[2])) {
            //TODO display this on the scoll view
            List<Integer> ids = db.getUserInactiveAccountIdsHelper(customer);
            ArrayAdapter adapter = new ArrayAdapter(appContext, android.R.layout.simple_list_item_1, ids);
            accountIds.setAdapter(adapter);
        }
          break;
    }
  }
}
