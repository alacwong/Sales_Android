package com.b07.salesandroid.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;


import androidx.appcompat.app.AppCompatActivity;

import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.exceptions.ExceptionHandler;
import com.b07.exceptions.UserNotFoundException;
import com.b07.salesandroid.R;
import com.b07.salesandroid.controllers.AccountButtonController;
import com.b07.salesandroid.models.AccountModel;
import com.b07.users.Customer;
import com.b07.users.Roles;
import com.b07.users.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AccountView extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Account View");
    setContentView(R.layout.account_view);
    ArrayList<CharSequence> customers = AccountModel.getCustomerIds(this);
    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customers);

    for (CharSequence s : customers) {
      System.out.println("asdf" + s + "adsf");
    }

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    Spinner drop = findViewById(R.id.customerAccounts);
    drop.setAdapter(adapter);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    Button showAccount = findViewById(R.id.showAccount);
    showAccount.setOnClickListener(new AccountButtonController(this));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

}
