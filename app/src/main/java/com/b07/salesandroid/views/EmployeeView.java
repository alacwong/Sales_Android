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
import com.b07.salesandroid.controllers.EmployeeButtonController;
import com.b07.users.Employee;
import com.b07.users.User;


public class EmployeeView extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Employee View");
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    setContentView(R.layout.employee_view);

    User user = (Employee) getIntent().getSerializableExtra("User");
    EmployeeButtonController employeeButtonController = new EmployeeButtonController(this,
        user, findViewById(R.id.employeeView));

    Button authenticateNewEmployee = findViewById(R.id.employeeAuthenticateNewEmployee);
    authenticateNewEmployee.setOnClickListener(
        employeeButtonController);

    Button createNewUser = findViewById(R.id.employeeCreateNewUser);
    createNewUser.setOnClickListener(employeeButtonController);

    Button createNewAccount = findViewById(R.id.employeeCreateNewAccount);
    createNewAccount.setOnClickListener(employeeButtonController);

    Button createNewEmployee = findViewById(R.id.employeeCreateNewEmployee);
    createNewEmployee.setOnClickListener(employeeButtonController);

    Button restockInventory = findViewById(R.id.employeeRestockInventory);
    restockInventory.setOnClickListener(employeeButtonController);
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
