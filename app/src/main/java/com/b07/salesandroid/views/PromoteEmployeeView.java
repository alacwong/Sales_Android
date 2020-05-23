package com.b07.salesandroid.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.b07.salesandroid.R;
import com.b07.salesandroid.controllers.AdminButtonController;

public class PromoteEmployeeView extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTitle("Promote Employee");
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    setContentView(R.layout.promote_employee_view);
    Spinner employeeListSpinner = findViewById(R.id.employeeListSpinner);
    // need to replace this code with SQLite database code ver when ready (check discord for link)
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.dummyStrings, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    employeeListSpinner.setAdapter(adapter);

    Button promoteEmployee = findViewById(R.id.promoteEmployee);
    promoteEmployee.setOnClickListener(new AdminButtonController(this));
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
