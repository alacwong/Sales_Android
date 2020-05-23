package com.b07.salesandroid.controllers;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.exceptions.InputException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.salesandroid.R;
import com.b07.salesandroid.models.CustomerModel;
import com.b07.salesandroid.views.ShoppingCartView;
import com.b07.users.Account;
import com.b07.users.User;

/**
 * This button controller is for Customer commands.
 */

public class CustomerButtonController extends AppCompatActivity implements View.OnClickListener {

  private Context appContext;
  private CustomerModel customerModel;

  public CustomerButtonController(Context context, int accountId, User customer) {
    appContext = context;
    DatabaseAndroidHelper databaseAndroidHelper = new DatabaseAndroidHelper(context);
    try {
      customerModel = new CustomerModel(
          databaseAndroidHelper.getAccountDetailsHelper(accountId, customer));
    } catch (InputException | ItemNotFoundException error) {
      //big issue
    }
    databaseAndroidHelper.close();
  }

  @Override
  public void onClick(View view) {
    Intent intent;
    switch (view.getId()) {
      case R.id.viewCart:
        intent = new Intent(this.appContext, ShoppingCartView.class);
        intent.putExtra("account", customerModel.getAccount());
        appContext.startActivity(intent);
        break;
      case R.id.addItems:
        alertDialogueAddItem("Add Item", appContext);
        break;
      case R.id.removeItems:
        alertDialogueRemoveItem("Add Item", appContext);
        break;
      case R.id.totalPrice:
        customerModel.viewTotalPrice("Total Price", appContext);
        break;
      case R.id.refund:
        break;
      case R.id.saveCart:
        System.out.println("listen");
        if (customerModel.saveCart(appContext)){
          customerModel.toastUser("success", appContext);
        }
        break;
    }
  }

  public void alertDialogueAddItem(String title, final Context context) {
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
          customerModel.addItem(itemId, quantity, context);
        } catch (NumberFormatException error) {
          customerModel.toastUser("Invalid Input", context);
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


  public void alertDialogueRemoveItem(String title, final Context context) {
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
          customerModel.removeItem(itemId, quantity, context);
        } catch (NumberFormatException error) {
          customerModel.toastUser("Invalid Input", context);
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
