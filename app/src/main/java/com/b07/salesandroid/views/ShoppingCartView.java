package com.b07.salesandroid.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;


import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.database.helper.DatabaseUpdateHelper;
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
import com.b07.salesandroid.R;
import com.b07.salesandroid.controllers.CustomerButtonController;
import com.b07.store.ShoppingCart;
import com.b07.users.Account;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;

public class ShoppingCartView extends AppCompatActivity implements View.OnClickListener {

  Account account;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //DatabaseDriverAndroid db = new com.b07.database.DatabaseDriverAndroid(this);

    setTitle("Shopping Cart");
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    setContentView(R.layout.shopping_cart);
    System.out.println("cart");
    account = (Account) getIntent().getSerializableExtra("account");
    TextView cart = (TextView)findViewById(R.id.shoppingCartContentTextview);
    cart.setText(getCartContent());
    findViewById(R.id.saveCart).setOnClickListener(this);
    findViewById(R.id.checkout).setOnClickListener(this);

    /* Example from Android guide: https://developer.android.com/guide/topics/ui/declaring-layout.html#FillingTheLayout
     *
     * */
  }

  public String getCartContent(){
    String content = "Item\t\t\tQuantity\n";
    HashMap<Item, Integer> cart = account.getCart().getMap();
    for (Item item: cart.keySet()){
      content+= item.getName() + "\t\t\t" + cart.get(item).toString() + "\r\n";
    }
    content += "Total\t\t\t $ " + account.getCart().getTotal().toString();
    return content;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    this.finish();
    return true;
  }

  public boolean saveCart(){
    DatabaseAndroidHelper db = new DatabaseAndroidHelper(this);
    System.out.println("I got here");
    try {
      for (Item item : account.getCart().getItems()) {
        db.insertAccountLineHelper(account.getId(), item.getId(), account.getCart().getMap().get(item));
      }
    } catch(InputException e){
      toastUser(ExceptionHandler.handle(e), this);
      return false;
    }
    return true;
  }
  public void onClick(View view){
    switch (view.getId()){
      case R.id.saveCart:
        if (saveCart()){
          toastUser("success", this);
        }
        break;
      case R.id.checkout:
        if (checkout()){
          toastUser("success", this);
        }
        break;
    }
  }

  public boolean checkout(){
    DatabaseAndroidHelper db  = new DatabaseAndroidHelper(this);
    try {
      ShoppingCart cart = account.getCart();

      for (Item item : cart.getItems() ) {
        if (db.getInventoryHelper().getItemMap().get(item) <  cart.getMap().get(item)) {
          throw new InsufficientInventoryException();
        }
      }
      for (Item item : cart.getItems()) {
        db.updateInventoryQuantityHelper(db.getInventoryHelper().getItemMap().get(item) -
                cart.getMap().get(item), item.getId());
      }
      System.out.println(account.getCustomer() == null);
      System.out.println(account.getCustomer().getId());
      System.out.println(account.getCustomer().getName());
      int saleId = db.insertSaleHelper(account.getCustomer().getId(), cart.getTotal());
      for (Item item : cart.getItems()) {
        db.insertItemizedSaleHelper(saleId, item.getId(),  cart.getMap().get(item));
      }

      cart.clearCart();
      TextView content = (TextView)findViewById(R.id.shoppingCartContentTextview);
      content.setText(getCartContent());
      db.updateAccountStatusHelper(account.getId(), false);
    } catch(InsufficientInventoryException | ItemNotFoundException
            | InputException | IncorrectActivityException e){

      toastUser(ExceptionHandler.handle(e), this);
      return false;
    }
    return true;
  }

  public void toastUser(String message, Context context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }
}


