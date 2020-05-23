package com.b07.salesandroid.controllers;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.b07.salesandroid.R;
import com.b07.salesandroid.models.EmployeeModel;
import com.b07.users.Roles;
import com.b07.users.User;

public class EmployeeButtonController extends AppCompatActivity implements View.OnClickListener {

  private Context appContext;
  private EmployeeModel employeeModel;

  public EmployeeButtonController(Context context, User initialEmployee, View view) {
    this.appContext = context;
    this.employeeModel = new EmployeeModel(context, initialEmployee, view);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.employeeAuthenticateNewEmployee:
        alertDialogueNewEmployee("Authenicate New Employee", appContext, view);
        break;
      case R.id.employeeCreateNewUser:
        alertDialogueNewUserOrEmployee("Create New User", Roles.CUSTOMER, appContext);
        break;
      case R.id.employeeCreateNewAccount:
        alertDialogueNewAccount("Create New Account", appContext);
        break;
      case R.id.employeeCreateNewEmployee:
        alertDialogueNewUserOrEmployee("Create New Employee", Roles.EMPLOYEE, appContext);
        break;
      case R.id.employeeRestockInventory:
        alertDialogueRestockInventory("Restock Inventory", appContext);
        break;
      default:
        break;
    }
  }

  public void alertDialogueNewEmployee(String title, final Context context, final View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title);
    LinearLayout inputs = new LinearLayout(context);
    inputs.setOrientation(LinearLayout.VERTICAL);
    // Set up the input
    final EditText idInput = new EditText(context);
    final EditText passwordInput = new EditText(context);
    // Specify the type of input expected
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint("Enter ID");
    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    passwordInput.setHint("Enter Password");
    inputs.addView(idInput);
    inputs.addView(passwordInput);
    builder.setView(inputs);

    // Set up the buttons
    builder.setPositiveButton("Enter", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        int id = -1010102;
        try {
          id = parseInt(idInput.getText().toString());
        } catch (NumberFormatException error) {
          employeeModel.toastUser("Invalid Input", context);
          dialog.cancel();
        }
        String password = passwordInput.getText().toString();
        employeeModel.authenicateNewEmployee(id, password, context, view);

      }
    });
    builder.setNegativeButton("Cancel", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
  }

  public void alertDialogueNewUserOrEmployee(String title, Roles roles, final Context context) {
    final Roles roleToMake = roles;
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title);
    LinearLayout inputs = new LinearLayout(context);
    inputs.setOrientation(LinearLayout.VERTICAL);
    // Set up the input
    final EditText nameInput = new EditText(context);
    nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
    nameInput.setHint("Enter Name");
    inputs.addView(nameInput);

    final EditText ageInput = new EditText(context);
    ageInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    ageInput.setHint("Enter Age");
    inputs.addView(ageInput);

    final EditText addressInput = new EditText(context);
    addressInput.setInputType(InputType.TYPE_CLASS_TEXT);
    addressInput.setHint("Enter Address");
    inputs.addView(addressInput);

    final EditText passwordInput = new EditText(context);
    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    passwordInput.setHint("Enter Password");
    inputs.addView(passwordInput);

    builder.setView(inputs);

    // Set up the buttons
    builder.setPositiveButton("Enter", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String name = nameInput.getText().toString();
        String address = addressInput.getText().toString();
        String password = passwordInput.getText().toString();
        int age;
        try {
          age = parseInt(ageInput.getText().toString());
          if (roleToMake == Roles.CUSTOMER) {
            employeeModel.createNewUser(name, age, address, password, context);
          } else if (roleToMake == Roles.EMPLOYEE) {
            employeeModel.createNewEmployee(name, age, address, password, context);
          }
        } catch (NumberFormatException error) {
          employeeModel.toastUser("Invalid Input", context);
          dialog.cancel();
        }
      }
    });
    builder.setNegativeButton("Cancel", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
  }

  public void alertDialogueNewAccount(String title, final Context context) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title);
    LinearLayout inputs = new LinearLayout(context);
    inputs.setOrientation(LinearLayout.VERTICAL);
    // Set up the input
    final EditText idInput = new EditText(context);
    // Specify the type of input expected
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint("Enter ID of User");
    inputs.addView(idInput);
    builder.setView(inputs);

    // Set up the buttons
    builder.setPositiveButton("Enter", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        int id;
        try {
          id = parseInt(idInput.getText().toString());
          employeeModel.createNewAccount(id, context);
        } catch (NumberFormatException error) {
          employeeModel.toastUser("Invalid Input", context);
          dialog.cancel();
        }
      }
    });
    builder.setNegativeButton("Cancel", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
  }

  public void alertDialogueRestockInventory(String title, final Context context) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title);
    LinearLayout inputs = new LinearLayout(context);
    inputs.setOrientation(LinearLayout.VERTICAL);
    // Set up the input
    final EditText itemIdInput = new EditText(context);
    final EditText quantityInput = new EditText(context);
    // Specify the type of input expected
    itemIdInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    itemIdInput.setHint("Enter Item Id");
    quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    quantityInput.setHint("Enter Quantity");
    inputs.addView(itemIdInput);
    inputs.addView(quantityInput);
    builder.setView(inputs);

    // Set up the buttons
    builder.setPositiveButton("Enter", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        int itemId;
        int quantity;
        try {
          itemId = parseInt(itemIdInput.getText().toString());
          quantity = parseInt(quantityInput.getText().toString());
          employeeModel.restockInventory(itemId, quantity, context);
        } catch (NumberFormatException error) {
          employeeModel.toastUser("Invalid Input", context);
          dialog.cancel();
        }
      }
    });
    builder.setNegativeButton("Cancel", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
  }
}