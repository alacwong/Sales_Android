package com.b07.users;

import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseUpdateHelper;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.ExceptionFactory;
import com.b07.exceptions.ExceptionHandler;
import com.b07.exceptions.InputException;
import com.b07.inventory.Inventory;
import com.b07.inventory.Item;
import java.sql.SQLException;

/*
 * - currentEmployee : Employee - inventory : Inventory + EmployeeInterface ( employee : Employee,
 * inventory : Inventory) CSCB07 Fall 20179 Assignment October 21 2019 + EmployeeInterface
 * (inventory : Inventory) + setCurrentEmployee(employee : Employee) : void + hasCurrentEmployee() :
 * boolean + restockInventory(item : Item, quantity : int) : boolean + createCustomer (name :
 * String, age : int, address : String, password : String) : int + createEmployee(name : String, age
 * : int, address : String, password : String) : int
 */
public class EmployeeInterface {

  private Employee currentEmployee;
  private Inventory inventory;

  public EmployeeInterface(Inventory inventory) {
    this.inventory = inventory;
  }

  public void setCurrentEmployee(Employee employee) {
    this.currentEmployee = employee;
  }

  public Employee getCurrentEmployee() {
    return this.currentEmployee;
  }

  public boolean hasCurrentEmployee() {
    return !(currentEmployee == null);
  }

  /**
   * Restock the invetory.
   * 
   * @param item item to be changed
   * @param quantity how much of that item to be changed
   * @return if operation was successful.
   */
  public boolean restockInventory(Item item, int quantity) {

    if (item == null) {
      return false;
    } else if (!inventory.getItemMap().containsKey(item)) {
      return false;
    } else if (quantity < 0) {
      return false;
    }
    int newQuantity = inventory.getItemMap().get(item) + quantity;
    try {
      DatabaseUpdateHelper.updateInventoryQuantity(newQuantity, item.getId());
      inventory.getItemMap().put(item, newQuantity);
    } catch (SQLException | InputException error) {
      ExceptionHandler.handle(error);
      return false;
    }
    return true;
  }

  /**
   * If currentEmployee exists and is authenticated, creates an Employee with the given parameters.
   * 
   * @param name of the new user.
   * @param age of the new user.
   * @param address of the new user.
   * @param password of the new user.
   * @return userId new user's id.
   * @throws InputException inputs invalid.
   * @throws DatabaseInsertException error inserting in database.
   * @throws SQLException error regarding database.
   * @throws Exception but will throw RoleIdNotFound if role name not found or SQLException if there
   *         was an error connecting to the database.
   */
  public int createEmployee(String name, int age, String address, String password)
      throws SQLException, DatabaseInsertException, InputException {
    if (hasCurrentEmployee() && getCurrentEmployee().isAuthenticated()) {
      return createUser(name, age, address, password, Roles.EMPLOYEE);
    } else {
      throw ExceptionFactory.createException("AuthenticationException");
    }
  }

  /**
   * If currentEmployee exists and is authenticated, creates a Customer with the given parameters.
   * 
   * @param name of the new user.
   * @param age of the new user.
   * @param address of the new user.
   * @param password of the new user.
   * @return userId new user's id.
   * @throws InputException inputs invalid.
   * @throws DatabaseInsertException error inserting in database.
   * @throws SQLException error regarding database.
   * @throws Exception but will throw RoleIdNotFound if role name not found or SQLException if there
   *         was an error connecting to the database.
   */
  public int createCustomer(String name, int age, String address, String password)
      throws SQLException, DatabaseInsertException, InputException {
    if (hasCurrentEmployee() && getCurrentEmployee().isAuthenticated()) {
      return createUser(name, age, address, password, Roles.CUSTOMER);
    } else {
      throw ExceptionFactory.createException("AuthenticationException");
    }
  }

  /**
   * Creates a user with name, age, address, and password.
   * 
   * @param name of the new user.
   * @param age of the new user.
   * @param address of the new user.
   * @param password of the new user.
   * @return userId new user's id.
   * @throws InputException inputs invalid.
   * @throws DatabaseInsertException error inserting in database.
   * @throws SQLException error regarding database.
   * @throws Exception but will throw RoleIdNotFound if role name not found or SQLException if there
   *         was an error connecting to the database.
   */
  public static int createUser(String name, int age, String address, String password, Roles role)
      throws SQLException, DatabaseInsertException, InputException {
    int userId = DatabaseInsertHelper.insertNewUser(name, age, address, password);
    int roleId = DatabaseInsertHelper.insertRole(role.toString());
    DatabaseInsertHelper.insertUserRole(userId, roleId);
    return userId;
  }
}
