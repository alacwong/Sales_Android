package com.b07.database.helper;

import com.b07.database.DatabaseInserter;
import com.b07.database.helper.DatabaseDriverHelper;
import com.b07.exceptions.ConnectionFailedException;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.ExceptionFactory;
import com.b07.exceptions.InputException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.users.Roles;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

// check for unique combinations

public class DatabaseInsertHelper extends DatabaseInserter {

  private static ArrayList<Integer> combinations = new ArrayList<>();

  /**
   * Add role to db.
   * 
   * @param name name to be changed
   * @return id of new role
   * @throws SQLException if data is not in db
   */

  public static int insertRole(String name) throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();

    int roleId = -1;
    try {
      roleId = DatabaseInserter.insertRole(name, connection);
    } catch (DatabaseInsertException e) {
      e.printStackTrace();
    }
    connection.close();
    return roleId;
  }

  /**
   * Inserts user after checking data.
   * 
   * @param name name of user
   * @param age age of user
   * @param address address of user
   * @param password password of use
   * @return return the if of the user
   * @throws SQLException if data is not db
   * @throws DatabaseInsertException if data could not be properly inserted
   * @throws InputException if input is invalid
   */
  public static int insertNewUser(String name, int age, String address, String password)
      throws SQLException, DatabaseInsertException, InputException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();

    if (name == null || address == null || password == null) {
      throw ExceptionFactory.createException("NullInputException");
    }

    if (!(removeSpace(name).matches("[a-zA-Z]+"))) {
      throw ExceptionFactory.createException("InvalidNameException");
    } else if (age < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    } else if (address.length() > 100) {
      throw ExceptionFactory.createException("StringLengthException");
    } else if (!removeSpace(address).matches("^[a-zA-Z0-9]+")) {
      throw ExceptionFactory.createException("InvalidNameException");
    }

    int userId = DatabaseInserter.insertNewUser(name, age, address, password, connection);
    connection.close();
    return userId;
  }

  /**
   * Inserts user without hashing password after checking data.
   * 
   * @param name name of user
   * @param age age of user
   * @param address address of user
   * @param password password of use
   * @return return the if of the user
   * @throws SQLException if data is not db
   * @throws DatabaseInsertException if data could not be properly inserted
   * @throws InputException if data is invalid
   */
  public static int portNewUser(String name, int age, String address, String password)
      throws SQLException, DatabaseInsertException, InputException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();

    if (name == null || address == null || password == null) {
      throw ExceptionFactory.createException("NullInputException");
    }

    if (!(removeSpace(name).matches("[a-zA-Z]+"))) {
      throw ExceptionFactory.createException("InvalidNameException");
    } else if (age < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    } else if (address.length() > 100) {
      throw ExceptionFactory.createException("StringLengthException");
    } else if (!removeSpace(address).matches("^[a-zA-Z0-9]+")) {
      throw ExceptionFactory.createException("InvalidNameException");
    }

    int userId = DatabaseInserter.portNewUser(name, age, address, password, connection);
    connection.close();
    return userId;
  }

  private static String removeSpace(String name) {
    String temp = "";
    for (String s : name.split(" ")) {
      temp += s;
    }
    return temp;
  }

  /**
   * Add role to db.
   * 
   * @param userId id of user
   * @param roleId if of role
   * @return id role
   * @throws DatabaseInsertException error in insertion
   * @throws SQLException data not in db
   */

  public static int insertUserRole(int userId, int roleId)
      throws DatabaseInsertException, SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int userRoleId = DatabaseInserter.insertUserRole(userId, roleId, connection);
    connection.close();
    return userRoleId;
  }

  /**
   * Put item into db.
   * 
   * @param name name of item
   * @param price price of item
   * @return item id
   * @throws DatabaseInsertException error in insertion
   * @throws SQLException if not in db
   * @throws InputException error regarding bad inputs
   */
  public static int insertItem(String name, BigDecimal price)
      throws DatabaseInsertException, SQLException, InputException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    if (name == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (name.length() > 63) {
      throw ExceptionFactory.createException("StringLengthException");
    } else if (!removeSpace(name).matches("^[a-zA-Z]*$")) {
      throw ExceptionFactory.createException("InvalidNameException");
    }
    if (price == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (price.toString().split("\\.")[1].length() != 2) {
      throw ExceptionFactory.createException("PrecisionException");
    } else if (price.floatValue() < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    int itemId = DatabaseInserter.insertItem(name, price, connection);
    connection.close();
    return itemId;
  }

  /**
   * add inventroy to db.
   * 
   * @param itemId id of item
   * @param quantity quantity of item
   * @return inventory id
   * @throws ConnectionFailedException cannot connect to db
   * @throws DatabaseInsertException insertion error
   * @throws SQLException data not in db
   * @throws ItemNotFoundException item not in db
   * @throws InputException error regarding bad inputs
   */
  public static int insertInventory(int itemId, int quantity) throws ConnectionFailedException,
      DatabaseInsertException, SQLException, ItemNotFoundException, InputException {
    if (quantity < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int inventoryId = DatabaseInserter.insertInventory(itemId, quantity, connection);
    connection.close();
    return inventoryId;
  }

  /**
   * Adds sale to db.
   * 
   * @param userId id of user of sale
   * @param totalPrice price of sale
   * @return sale id
   * @throws UserNotFoundException if no user is detected
   * @throws DatabaseInsertException insertion error in database
   * @throws SQLException if data is not in db
   * @throws SaleNotFoundException error for wanted sale not found
   * @throws ItemNotFoundException error for wanted item not found
   * @throws InputException error regarding bad inputs
   */
  public static int insertSale(int userId, BigDecimal totalPrice)
      throws UserNotFoundException, DatabaseInsertException, SQLException, ItemNotFoundException,
      SaleNotFoundException, InputException {
    if (totalPrice == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (totalPrice.floatValue() < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    } else if (totalPrice.scale() != 2) {
      throw ExceptionFactory.createException("PrecisionException");
    }

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int saleId = DatabaseInserter.insertSale(userId, totalPrice, connection);
    connection.close();
    return saleId;
  }
  
  /**
   * Inserts a refund into the database.
   * @param userId id of the user
   * @param totalPrice total price of the refund_view
   * @return The id of the sale
   * @throws UserNotFoundException if no user is detected
   * @throws DatabaseInsertException insertion error in database
   * @throws SQLException if data is not in db
   * @throws SaleNotFoundException error for wanted sale not found
   * @throws ItemNotFoundException error for wanted item not found
   * @throws InputException error regarding bad inputs
   */
  public static int insertRefund(int userId, BigDecimal totalPrice)
      throws UserNotFoundException, DatabaseInsertException, SQLException, ItemNotFoundException,
      SaleNotFoundException, InputException {
    if (totalPrice == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (totalPrice.scale() != 2) {
      throw ExceptionFactory.createException("PrecisionException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int saleId = DatabaseInserter.insertSale(userId, totalPrice, connection);
    connection.close();
    return saleId;
  }

  // private static boolean checkPrice(BigDecimal totalPrice, List<Sale> sales) {
  // BigDecimal total = new BigDecimal("0.00");
  // for (Sale sale: sales) {
  // for (Item item: sale.getItemMap().keySet()) {
  // total = total.add(new BigDecimal(item.getPrice().toString())
  // .multiply(new BigDecimal(sale.getItemMap().get(item).toString())));
  // }
  // }
  // System.out.println(total + " " + totalPrice);
  // return total.toString().equals(totalPrice.toString());
  // }

  /**
   * put itemized sale into db.
   * 
   * @param saleId id of sale
   * @param itemId id of item
   * @param quantity qunatity of item
   * @return id of sale
   * @throws SQLException if not in db
   * @throws SaleNotFoundException if sale is not found
   * @throws ItemNotFoundException if item is not found
   * @throws DatabaseInsertException if error in insertion
   * @throws InputException thrown for bad inputs
   */
  public static int insertItemizedSale(int saleId, int itemId, int quantity) throws SQLException,
      SaleNotFoundException, ItemNotFoundException, DatabaseInsertException, InputException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    if (quantity < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    } else if (combinations.contains(new Integer(saleId + itemId))) {
      throw ExceptionFactory.createException("InvalidCombinationException");
    }
    int itemizedId = DatabaseInserter.insertItemizedSale(saleId, itemId, quantity, connection);
    connection.close();
    return itemizedId;
  }
  
  /**
   * inserts an itemized refund_view into database.
   * @param saleId is the id of the sale
   * @param itemId is the id of the item
   * @param quantity of the item
   * @return Itemized sale (refund_view) id
   * @throws SQLException if not in db
   * @throws SaleNotFoundException if sale is not found
   * @throws ItemNotFoundException if item is not found
   * @throws DatabaseInsertException if error in insertion
   * @throws InputException thrown for bad inputs
   */
  public static int insertItemizedRefund(int saleId, int itemId, int quantity) throws SQLException,
      SaleNotFoundException, ItemNotFoundException, DatabaseInsertException, InputException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    if (combinations.contains(new Integer(saleId + itemId))) {
      throw ExceptionFactory.createException("InvalidCombinationException");
    }
    int itemizedId = DatabaseInserter.insertItemizedSale(saleId, itemId, quantity, connection);
    connection.close();
    return itemizedId;
  }

  /**
   * inserting account into database.
   * 
   * @param userId id of user connected to the account
   * @return id of the account
   * @throws DatabaseInsertException error regarding inserting data into database
   * @throws SQLException error regarding sql database
   * @throws InputException error regarding bad inputs
   * @throws UserNotFoundException error for wanted user not found
   */
  @Deprecated
  public static int insertAccount(int userId)
      throws DatabaseInsertException, SQLException, InputException, UserNotFoundException {
    DatabaseSelectHelper.getUserDetails(userId);
    if (!DatabaseSelectHelper.getRoleName(DatabaseSelectHelper.getUserRoleId(userId))
        .equals(Roles.CUSTOMER.toString())) {
      throw ExceptionFactory.createException("InvalidRoleException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int accountId = DatabaseInserter.insertAccount(userId, connection);
    connection.close();
    return accountId;
  }
  
  /**
   * inserts an account into the database.
   * @param userId users id
   * @param active active status of the user
   * @return id of the account
   * @throws DatabaseInsertException error regarding inserting data into database
   * @throws SQLException error regarding sql database
   * @throws InputException error regarding bad inputs
   * @throws UserNotFoundException error for wanted user not found
   */
  public static int insertAccount(int userId, boolean active)
      throws DatabaseInsertException, SQLException, InputException, UserNotFoundException {
    DatabaseSelectHelper.getUserDetails(userId);
    if (!DatabaseSelectHelper.getRoleName(DatabaseSelectHelper.getUserRoleId(userId))
        .equals(Roles.CUSTOMER.toString())) {
      throw ExceptionFactory.createException("InvalidRoleException");
    }
    if (!active) {
      throw ExceptionFactory.createException("AccountNotFoundException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int accountId = DatabaseInserter.insertAccount(userId, active, connection);
    connection.close();
    return accountId;
  }
  
  /**
   * inserts account into db.
   * @param userId id of the user
   * @param active active status of the user
   * @return the id of the account
   * @throws DatabaseInsertException error regarding inserting data into database
   * @throws SQLException error regarding sql database
   * @throws InputException error regarding bad inputs
   * @throws UserNotFoundException error for wanted user not found
   */
  public static int portAccount(int userId, boolean active)
      throws DatabaseInsertException, SQLException, InputException, UserNotFoundException {
    DatabaseSelectHelper.getUserDetails(userId);
    if (!DatabaseSelectHelper.getRoleName(DatabaseSelectHelper.getUserRoleId(userId))
          .equals(Roles.CUSTOMER.toString())) {
    throw ExceptionFactory.createException("InvalidRoleException");
  }
  Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
  int accountId = DatabaseInserter.insertAccount(userId, active, connection);
    connection.close();
    return accountId;
}
  
  /**
   * saves the shopping cart by inserting the items in the shopping cart into the database.
   * @param accountId id of the account
   * @param itemId id of the item
   * @param quantity of the item
   * @return id of a transaction which puts an item into a specific account
   * @throws DatabaseInsertException error regarding inserting data into database
   * @throws SQLException error regarding sql database
   * @throws InputException error regarding bad inputs
   */
  public static int insertAccountLine(int accountId, int itemId, int quantity)
      throws InputException, DatabaseInsertException, SQLException {
    if (quantity < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int id = DatabaseInsertHelper.insertAccountLine(accountId, itemId, quantity, connection);
    connection.close();
    return id;
  }
}
