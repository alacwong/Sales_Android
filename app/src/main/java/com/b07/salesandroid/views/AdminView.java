package com.b07.salesandroid.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.b07.salesandroid.R;
import com.b07.salesandroid.controllers.TransitionButtonController;
import com.b07.users.Admin;
import com.b07.users.User;

public class AdminView extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Admin View");

     User admin = (Admin) getIntent().getSerializableExtra("User");
      System.out.println(admin.getName() +  " " + admin.getId());
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    setContentView(R.layout.admin_view);

    Button promoteEmployee = findViewById(R.id.promoteEmployeePageButton);
    promoteEmployee.setOnClickListener(new TransitionButtonController(this));

    Button getAccountView = findViewById(R.id.accountsButton);
    getAccountView.setOnClickListener(new TransitionButtonController(this));
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