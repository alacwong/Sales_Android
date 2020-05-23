package com.b07.salesandroid.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.b07.exceptions.AuthenticationException;
import com.b07.exceptions.ExceptionHandler;
import com.b07.exceptions.UserNotFoundException;
import com.b07.salesandroid.R;
import com.b07.salesandroid.models.AuthenticationModel;
import com.b07.salesandroid.views.AccountView;
import com.b07.salesandroid.views.InitializeView;
import com.b07.salesandroid.views.LoginView;
import com.b07.salesandroid.views.PromoteEmployeeView;
import com.b07.salesandroid.views.SalesHistoryView;

/**
 * This button controller is for switching Activities.
 */
public class TransitionButtonController implements View.OnClickListener {

  private Context appContext;

  public TransitionButtonController(Context context) {
    appContext = context;
  }

  @Override
  public void onClick(View view) {
    Intent intent;
    switch (view.getId()) {
      // Home Page
      case R.id.loginPageButton:
        intent = new Intent(this.appContext, LoginView.class);
        appContext.startActivity(intent);
        break;

      case R.id.initializeButton:
        intent = new Intent(this.appContext, InitializeView.class);
        appContext.startActivity(intent);
        break;

      // Login Page
      case R.id.submitLogin:
        EditText username = ((Activity) appContext).findViewById(R.id.userName);
        int userId;
        try {
          userId = Integer.parseInt(username.getText().toString());
          EditText password = ((Activity) appContext).findViewById(R.id.password);
          String userPassword = password.getText().toString();
          intent = AuthenticationModel.authenticateUser(userId, userPassword, appContext);
          if (intent == null) {
            throw new AuthenticationException();
          }
          appContext.startActivity(intent);
        } catch (NumberFormatException error) {
          ((TextView) ((Activity) appContext).findViewById(R.id.errorLogin)).setText(
              "Invalid Input");
        } catch (UserNotFoundException | AuthenticationException error) {
          ((TextView) ((Activity) appContext).findViewById(R.id.errorLogin)).setText(
              ExceptionHandler.handle(error));
          Toast.makeText(appContext, ExceptionHandler.handle(error), Toast.LENGTH_SHORT);
        }
        break;

      // Admin Page
      case R.id.promoteEmployeePageButton:
        intent = new Intent(this.appContext, PromoteEmployeeView.class);
        appContext.startActivity(intent);
        break;
      case R.id.salesHistoryButton:
        intent = new Intent(this.appContext, SalesHistoryView.class);
        appContext.startActivity(intent);
        break;
      case R.id.accountsButton:
        System.out.println("New account");
        intent = new Intent(this.appContext, AccountView.class);
        appContext.startActivity(intent);
        break;

    }
  }
}
