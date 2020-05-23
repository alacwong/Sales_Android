package com.b07.database.helper;

import com.b07.database.DatabaseUpdater;
import com.b07.exceptions.ExceptionFactory;
import com.b07.exceptions.IncorrectActivityException;
import com.b07.exceptions.InputException;
import com.b07.exceptions.InvalidRoleException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.users.Roles;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUpdateHelper extends DatabaseUpdater {

  /**
   * update the role name of user with given id.
   * 
   * @param name name of role
   * @param id id of user
   * @return true if operation was successful, false otherwise
   * @throws InputException error for bad inputs
   * @throws SQLException error regarding sql database
   * @throws InvalidRoleException error regarding role outside of roles enumerator
   */
  public static boolean updateRoleName(String name, int id)
      throws InputException, SQLException, InvalidRoleException {
    if (name == null) {
      throw ExceptionFactory.createException("NullInputException");
    }
    if (Roles.ADMIN.toString().equals(name) && Roles.CUSTOMER.toString().equals(name)
        && Roles.EMPLOYEE.toString().equals(name)) {
      throw ExceptionFactory.createException("InvalidRoleException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateRoleName(name, id, connection);
    connection.close();
    return complete;
  }

  /**
   * updates a users name in the database.
   * 
   * @param name name of user
   * @param userId id of user
   * @return true if operation successful, false otherwise
   * @throws UserNotFoundException error for wanted user not found
   * @throws SQLException error regarding sql database
   * @throws InputException error regarding bad inputs
   */
  public static boolean updateUserName(String name, int userId)
      throws UserNotFoundException, SQLException, InputException {

    if (name == null) {
      throw ExceptionFactory.createException("NullInputException");
    }
    String temp = "";
    for (String s : name.split(" ")) {
      temp += s;
    }
    if (!temp.matches("^[a-zA-Z]*$")) {
      throw ExceptionFactory.createException("InvalidNameException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateUserName(name, userId, connection);
    connection.close();
    return complete;
  }

  /**
   * updates the age of a given user.
   * 
   * @param age age to update to
   * @param userId id of user
   * @return true if successful, false otherwise
   * @throws UserNotFoundException error for wanted user not found
   * @throws SQLException error regarding sql database
   * @throws InputException error regarding bad inputs
   */
  public static boolean updateUserAge(int age, int userId)
      throws UserNotFoundException, SQLException, InputException {
    if (age < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateUserAge(age, userId, connection);
    connection.close();
    return complete;
  }

  /**
   * updates the address of a given user.
   * 
   * @param address address to update to
   * @param userId id of user
   * @return true if successful, false otherwise
   * @throws UserNotFoundException error for user not found
   * @throws SQLException error regarding sql database
   * @throws InputException errore regarding bad inputs
   */
  public static boolean updateUserAddress(String address, int userId)
      throws UserNotFoundException, SQLException, InputException {

    if (address == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (address.length() > 100) {
      throw ExceptionFactory.createException("StringLengthException");
    } else if (!address.matches("^[a-zA-Z0-9]*$")) {
      throw ExceptionFactory.createException("InvalidNameException");
    }

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateUserAddress(address, userId, connection);
    connection.close();
    return complete;

  }

  /**
   * update user role with new roleId.
   * 
   * @param roleId id to update to
   * @param userId id of user
   * @return true if successful, false otherwise
   * @throws SQLException error regarding sql database
   */
  public static boolean updateUserRole(int roleId, int userId) throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateUserRole(roleId, userId, connection);
    connection.close();
    return complete;

  }

  /**
   * update the name of a given item.
   * 
   * @param name name to update to
   * @param itemId id of item
   * @return true if successful, false otherwise
   * @throws SQLException error regarding database
   * @throws InputException error regarding bad inputs
   */
  public static boolean updateItemName(String name, int itemId)
      throws SQLException, InputException {
    if (name == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (name.length() > 63) {
      throw ExceptionFactory.createException("StringLengthException");
    } else if (!name.matches("^[a-zA-Z]*$")) {
      throw ExceptionFactory.createException("InvalidNameException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateItemName(name, itemId, connection);
    connection.close();
    return complete;

  }

  /**
   * updates the price of a given item.
   * 
   * @param price new price
   * @param itemId id of item
   * @return true if successful, false otherwise
   * @throws SQLException error regarding sql database
   * @throws InputException error regarding bad inputs
   */
  public static boolean updateItemPrice(BigDecimal price, int itemId)
      throws SQLException, InputException {
    if (price == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (price.scale() != 2) {
      throw ExceptionFactory.createException("PrecisionException");
    } else if (price.floatValue() < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateItemPrice(price, itemId, connection);
    connection.close();
    return complete;
  }

  /**
   * updates the quantity of a given item in the database.
   * 
   * @param quantity new quantity
   * @param itemId id of item
   * @return true if successful, false otherwise
   * @throws SQLException error regarding sql database
   * @throws InputException error regarding bad inputs
   */
  public static boolean updateInventoryQuantity(int quantity, int itemId)
      throws SQLException, InputException {
    if (quantity < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateInventoryQuantity(quantity, itemId, connection);
    connection.close();
    return complete;
  }

  /**
   * update the status of the account (active/inactive).
   * 
   * @param accountId id of account
   * @param active status of active/inactive
   * @return true if successful, false otherwise
   * @throws SQLException error regarding sql database
   * @throws InputException error regarding bad inputs
   * @throws IncorrectActivityException error regarding modifying an inactive account
   */
  public static boolean updateAccountStatus(int accountId, boolean active)
      throws SQLException, InputException, IncorrectActivityException {
    if (active) {
      throw new IncorrectActivityException();
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateAccountStatus(accountId, false, connection);
    connection.close();
    return complete;
  }


}
