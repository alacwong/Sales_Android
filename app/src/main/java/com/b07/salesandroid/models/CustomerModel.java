package com.b07.salesandroid.models;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.exceptions.AccountNotFoundException;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.ExceptionHandler;
import com.b07.exceptions.IncorrectActivityException;
import com.b07.exceptions.InputException;
import com.b07.exceptions.InsufficientInventoryException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Item;
import com.b07.store.ShoppingCart;
import com.b07.users.Account;

import java.sql.SQLException;
import java.util.HashMap;

public class CustomerModel {

  private Account account;


  public CustomerModel(Account account){
    this.account = account;
  }
  public boolean addItem(int itemId, int quantity, Context context){
    DatabaseAndroidHelper db = new DatabaseAndroidHelper(context);
    try {
      account.getCart().addItem(db.getItemHelper(itemId), quantity);
      return true;
    } catch (InputException e){
      db.close();
      toastUser(ExceptionHandler.handle(e), context);
      return false;
    } catch (ItemNotFoundException e){
      db.close();
      toastUser(ExceptionHandler.handle(e), context);
      return false;
    }

  }

  public boolean removeItem(int itemId, int quantity, Context context){
    DatabaseAndroidHelper db = new DatabaseAndroidHelper(context);
    try {
      account.getCart().removeItem(db.getItemHelper(itemId), quantity);
      return true;
    } catch (InputException  |ItemNotFoundException|InsufficientInventoryException e){
      db.close();
      toastUser(ExceptionHandler.handle(e), context);
      return false;
    }
  }

  public boolean viewTotalPrice(String title, Context context){
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title);
    LinearLayout inputs = new LinearLayout(context);
    inputs.setOrientation(LinearLayout.VERTICAL);
    // Set up the input
    final TextView price = new TextView(context);
    // Specify the type of input expected
    String total = account.getCart().getTotal().toString();
    price.setText(String.format("$%s", total));
    inputs.addView(price);
    builder.setView(inputs);

    // Set up the buttons
    builder.setNegativeButton("Exit", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    builder.show();
    return false;
  }

  public boolean checkout(Context context){
    try {
      account.getCart().checkOut(account.getId());
      return true;
    } catch(InsufficientInventoryException| ItemNotFoundException| UserNotFoundException |
    InputException | SaleNotFoundException | DatabaseInsertException  | SQLException
            | AccountNotFoundException | IncorrectActivityException error){
      toastUser(ExceptionHandler.handle(error), context);
      return false;
    }

  }

  public Account getAccount(){
    return account;
  }

  public void toastUser(String message, Context context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  public boolean saveCart(Context context){
    DatabaseAndroidHelper db = new DatabaseAndroidHelper(context);
    System.out.println("I got here");
    try {
      for (Item item : account.getCart().getItems()) {
        db.insertAccountLineHelper(account.getId(), item.getId(), account.getCart().getMap().get(item));
      }
    } catch(InputException e){
      toastUser(ExceptionHandler.handle(e), context);
      return false;
    }
    return true;
  }

}
