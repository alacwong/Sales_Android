package com.b07.salesandroid.models;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.exceptions.ExceptionHandler;
import com.b07.exceptions.InputException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Inventory;
import com.b07.inventory.Item;
import com.b07.salesandroid.R;
import com.b07.security.PasswordHelpers;
import com.b07.users.Employee;
import com.b07.users.EmployeeInterface;
import com.b07.users.Roles;
import com.b07.users.User;

public class EmployeeModel {

  TextView employeeHead;

  private EmployeeInterface employeeInterface;

  public EmployeeModel(Context context, User intialEmployee, View view) {
    DatabaseAndroidHelper databaseAndroidHelper = new DatabaseAndroidHelper(context);
    ChangeHeaderText(intialEmployee.getName(), view);
    try {
      employeeInterface = new EmployeeInterface(databaseAndroidHelper.getInventoryHelper());
      employeeInterface.setCurrentEmployee((Employee) intialEmployee);

    } catch (ItemNotFoundException error) {
      toastUser(ExceptionHandler.handle(error), context);
    }
    databaseAndroidHelper.close();
  }

  private void ChangeHeaderText(String name, View view) {
    if (employeeHead == null) {
      employeeHead = view.findViewById(R.id.employeeHeadMessage);
    }
    employeeHead.setText("Welcome " + name + ", Select a Task:");
  }

  /**
   * authenitcates a new employee for the program.
   *
   * @param id id of the employee
   * @param password password of employee
   * @param context context of app
   * @return true if success false otherwise
   */
  public boolean authenicateNewEmployee(int id, String password, Context context, View view) {
    DatabaseAndroidHelper databaseAndroidHelper = new DatabaseAndroidHelper(context);
    try {
      User employee = databaseAndroidHelper.getUserDetailsHelper(id, context);
      System.out.println(employee.getName());

      if (employee.authenticate(password, context)){
        employeeInterface.setCurrentEmployee((Employee) employee);
        ChangeHeaderText(employee.getName(), view);
        toastUser("Authentication of User " + id + " Successful", context);
        return true;
      }
      toastUser("Invalid Password", context);
      return false;

    } catch (UserNotFoundException error) {
      //tell user input is not valid or id doesnt exist
      databaseAndroidHelper.close();
      toastUser(ExceptionHandler.handle(error), context);
      return false;
    }
  }

  public boolean createNewUser(String name, int age, String address, String password,
      Context context) {
    return createUserWithRole(name, age, address, password, context, Roles.CUSTOMER);
  }

  /**
   * creates a new accout for a given user.
   *
   * @param userId id of user
   * @param context app context
   * @return true if success, false otherwise
   */
  public boolean createNewAccount(int userId, Context context) {
    DatabaseAndroidHelper databaseAndroidHelper = new DatabaseAndroidHelper(context);
    try {
      int accountId = databaseAndroidHelper.insertAccountHelper(userId, true, context);
      databaseAndroidHelper.close();
      toastUser("Account Creation Successful: Account ID: " + accountId, context);
      return true;
    } catch (UserNotFoundException | InputException error) {
      databaseAndroidHelper.close();
      toastUser(ExceptionHandler.handle(error), context);
      return false;
    }
  }

  public boolean createNewEmployee(String name, int age, String address, String password,
      Context context) {
    return createUserWithRole(name, age, address, password, context, Roles.EMPLOYEE);
  }

  private boolean createUserWithRole(String name, int age, String address, String password,
      Context context, Roles role) {
    DatabaseAndroidHelper databaseAndroidHelper = new DatabaseAndroidHelper(context);
    //TODO remove comments on current employee check
    //    User currentEmployee = employeeInterface.getCurrentEmployee();
    //    if (currentEmployee != null && currentEmployee.isAuthenticated()) {
    try {
      int roleId = databaseAndroidHelper.insertRoleHelper(role.toString());
      ;
      int userId = databaseAndroidHelper.insertNewUserHelper(name, age, address, password);
      int id = databaseAndroidHelper.insertUserRoleHelper(userId, roleId);
      databaseAndroidHelper.close();
      toastUser(role.toString() + " Creation Successful, ID: " + id, context);
      return true;
    } catch (InputException error) {
      databaseAndroidHelper.close();
      toastUser(ExceptionHandler.handle(error), context);
      return false;
    }
    //    } else {
    //      databaseAndroidHelper.close();
    //      toastUser("Authentication Error", context);
    //      return false;
    //    }
  }

  /**
   * restocks the database inventory.
   *
   * @param itemId item to be restocked
   * @param quantity quantity to restock
   * @param context app context
   * @return true if success, false otherwise
   */
  public boolean restockInventory(int itemId, int quantity, Context context) {
    DatabaseAndroidHelper databaseAndroidHelper = new DatabaseAndroidHelper(context);
    try {
      Item item = databaseAndroidHelper.getItemHelper(itemId);
      Inventory inventory = databaseAndroidHelper.getInventoryHelper();
      if (item == null || inventory == null || !inventory.getItemMap().containsKey(item)
          || quantity < 0) {
        databaseAndroidHelper.close();
        toastUser("Item not Found", context);
        return false;
      }

      int newQuantity = inventory.getItemMap().get(item) + quantity;
      try {
        databaseAndroidHelper.updateInventoryQuantityHelper(newQuantity, item.getId());
        inventory.getItemMap().put(item, newQuantity);
        databaseAndroidHelper.close();
        toastUser("Restock Successful", context);
        return true;
      } catch (InputException error) {
        databaseAndroidHelper.close();
        toastUser(ExceptionHandler.handle(error), context);
        return false;
      }
    } catch (ItemNotFoundException error) {
      databaseAndroidHelper.close();
      toastUser(ExceptionHandler.handle(error), context);
      return false;
    }
  }

  public void toastUser(String message, Context context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }
}
