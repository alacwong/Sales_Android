package com.b07.salesandroid.models;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.exceptions.AuthenticationException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.salesandroid.views.AccountChoose;
import com.b07.salesandroid.views.AccountView;
import com.b07.salesandroid.views.AdminView;
import com.b07.salesandroid.views.CustomerView;
import com.b07.salesandroid.views.EmployeeView;
import com.b07.users.Roles;
import com.b07.users.User;
import java.sql.SQLException;

public class AuthenticationModel {

  public static Intent authenticateUser(int userId, String password, Context context) throws
      UserNotFoundException, AuthenticationException {
    DatabaseAndroidHelper db = new DatabaseAndroidHelper(context);
    User user = db.getUserDetailsHelper(userId, context);
    Intent intent;
    if (user.authenticate(password, context)) {
      if (db.getRoleHelper(userId).equals(Roles.ADMIN.toString())) {
        System.out.println("admin");
        intent = new Intent(context, AdminView.class);
      } else if (db.getRoleHelper(userId).equals(Roles.EMPLOYEE.toString())) {
        System.out.println("employee");
        intent = new Intent(context, EmployeeView.class);

      } else {
        System.out.println("customer");
        intent = new Intent(context, AccountChoose.class);
      }
      intent.putExtra("User", user);
      System.out.println(user.getName());
    } else {
      throw new AuthenticationException();
    }
    return intent;
  }
}
