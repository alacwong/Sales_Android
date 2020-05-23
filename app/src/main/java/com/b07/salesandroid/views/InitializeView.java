package com.b07.salesandroid.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.b07.database.DatabaseDriverAndroid;
import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.exceptions.ExceptionHandler;
import com.b07.exceptions.InputException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.ItemEnum;
import com.b07.salesandroid.R;
import com.b07.users.Roles;
import com.b07.users.User;
import java.math.BigDecimal;

import java.sql.SQLException;

public class InitializeView extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(R.string.initHeader);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    setContentView(R.layout.initialization);
    DatabaseDriverAndroid database = new DatabaseAndroidHelper(this);
    DatabaseAndroidHelper dbHelper = new DatabaseAndroidHelper(this);
    try {
      User Admin = dbHelper.getUserDetailsHelper(1, this);
      User employee = dbHelper.getUserDetailsHelper(2, this);
      System.out.println(dbHelper.getUserDetailsHelper(1, this).getName());
      System.out.println(dbHelper.getUserDetailsHelper(2, this).getName());
      TextView initText = findViewById(R.id.initText);
      initText.setText(dbHelper.getRoleHelper(1));
    } catch (Exception error) {
      try {
        int adminId = -1;
        int employeeId = -1;
        try {
          adminId = dbHelper.insertNewUserHelper("Alac", 19, "116 memon pl", "password");
          employeeId = dbHelper.insertNewUserHelper("Philip", 19, "Bur oak", "password");
        } catch (InputException e) {
          System.out.println(ExceptionHandler.handle(e));
        }
        int roleId = dbHelper.insertRoleHelper(Roles.ADMIN.toString());
        dbHelper.insertUserRoleHelper(adminId, roleId);
        int employeeRoleId = dbHelper.insertRoleHelper(Roles.EMPLOYEE.toString());
        dbHelper.insertUserRoleHelper(employeeId, employeeRoleId);

        try {
          int id1 =
                  dbHelper.insertItemHelper(ItemEnum.FISHING_ROD.toString(), new BigDecimal("5.57"));
          int id2 = dbHelper.insertItemHelper(ItemEnum.HOCKEY_STICK.toString(),
                  new BigDecimal("29.99"));
          int id3 =
                  dbHelper.insertItemHelper(ItemEnum.PROTEIN_BAR.toString(), new BigDecimal("99.99"));
          int id4 = dbHelper.insertItemHelper(ItemEnum.RUNNING_SHOES.toString(),
                  new BigDecimal("0.99"));
          int id5 =
                  dbHelper.insertItemHelper(ItemEnum.SKATES.toString(), new BigDecimal("129.99"));

          dbHelper.insertInventoryHelper(id1, 18);
          dbHelper.insertInventoryHelper(id2, 184);
          dbHelper.insertInventoryHelper(id3, 1);
          dbHelper.insertInventoryHelper(id4, 18);
          dbHelper.insertInventoryHelper(id5, 122);
          TextView initText = findViewById(R.id.initText);
          initText.setText(dbHelper.getUserDetailsHelper(1, this).getRoleId(this));
          Log.i("Console", dbHelper.getUserDetailsHelper(1, this).getName());
        } catch (InputException | UserNotFoundException e) {
          Log.e("Console", ExceptionHandler.handle(e));
        }
      } catch (Exception e) {
        System.out.println(ExceptionHandler.handle(e));
      }
      database.close();
//    initText.setText(getResources().getString(R.string.initFailure));
    }
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    this.finish();
    return true;
  }
}
