package com.b07.salesandroid.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.b07.salesandroid.R;
import com.b07.salesandroid.controllers.CustomerButtonController;
import com.b07.users.User;

public class CustomerView extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Customer View");
    User customer = (User) getIntent().getSerializableExtra("User");

    int account = (int) getIntent().getSerializableExtra("Account");
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    setContentView(R.layout.customer_view);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      System.out.println(customer == null);
      System.out.println(account);
    //TODO get proper account number
    CustomerButtonController customerButtonController = new CustomerButtonController(this, account,
            customer);

    Button cart = findViewById(R.id.viewCart);
    cart.setOnClickListener(customerButtonController);
    Button addItem = findViewById(R.id.addItems);
    addItem.setOnClickListener(customerButtonController);
    Button removeItem = findViewById(R.id.removeItems);
    removeItem.setOnClickListener(customerButtonController);
    Button viewTotalPrice = findViewById(R.id.totalPrice);
    viewTotalPrice.setOnClickListener(customerButtonController);
    Button refund = findViewById(R.id.refund);
    refund.setOnClickListener(customerButtonController);
    Button checkout = findViewById(R.id.checkout);
    checkout.setOnClickListener(customerButtonController);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    this.finish();
    return true;
  }
}
