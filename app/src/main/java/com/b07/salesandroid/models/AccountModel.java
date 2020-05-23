package com.b07.salesandroid.models;

import android.content.Context;
import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.users.Roles;
import com.b07.users.User;
import java.util.ArrayList;

public class AccountModel {
  public static ArrayList<CharSequence> getCustomerIds(Context context) {
    DatabaseAndroidHelper db = new DatabaseAndroidHelper(context);
    ArrayList<User> users = (ArrayList<User>) db.getUsersDetailsHelper(context);
    ArrayList<CharSequence> customers = new ArrayList<>();
    for (User user: users){
      if (db.getRoleHelper(user.getId()).equals(Roles.CUSTOMER.toString())){
        customers.add(user.getId() + ": " + user.getName());
      }
    }
    return customers;
  }
}
