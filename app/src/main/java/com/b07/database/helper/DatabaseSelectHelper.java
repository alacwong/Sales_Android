package com.b07.database.helper;

import com.b07.database.DatabaseSelector;
import com.b07.exceptions.InputException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.RoleIdNotFoundException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Inventory;
import com.b07.inventory.InventoryBuilder;
import com.b07.inventory.Item;
import com.b07.inventory.ItemImpl;
import com.b07.store.Sale;
import com.b07.store.SaleImpl;
import com.b07.store.SalesLog;
import com.b07.store.SalesLogImpl;
import com.b07.store.ShoppingCart;
import com.b07.users.Account;
import com.b07.users.Customer;
import com.b07.users.Roles;
import com.b07.users.User;
import com.b07.users.UserFactory;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * TODO: Complete the below methods to be able to get information out of the database. TODO: The
 * given code is there to aide you in building your methods. You don't have TODO: to keep the exact
 * code that is given (for example, DELETE the System.out.println()) TODO: and decide how to handle
 * the possible exceptions
 */
public class DatabaseSelectHelper extends DatabaseSelector {

  private static HashMap<Integer, Item> map = new HashMap<Integer, Item>();

  /**
   * Returns the ID of a role given its name.
   *
   * @param roleName the name of the Role (based on ENUM)
   * @return the ID of the role in the database
   * @throws RoleIdNotFoundException if the role ID was not found in the database.
   * @throws SQLException if there's an error connecting to the database.
   */
  public static int getRoleId(Roles roleName) throws RoleIdNotFoundException, SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    List<Integer> roleIds = getRoleIds();
    for (Integer roleId : roleIds) {
      if (getRoleName(roleId).equals(roleName.toString())) {
        connection.close();
        return roleId;
      }
    }
    // reached only if ID not found.
    throw new RoleIdNotFoundException();
  }

  /**
   * return all roleids.
   *
   * @return ids list of role ids
   * @throws SQLException data not in db
   */
  public static List<Integer> getRoleIds() throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getRoles(connection);
    List<Integer> ids = new ArrayList<>();
    while (results.next()) {
      ids.add(results.getInt("ID"));
    }
    results.close();
    connection.close();
    return ids;
  }

  /**
   * Name of role.
   *
   * @param roleId id of role.
   * @return role the role
   * @throws SQLException data not in db
   */

  public static String getRoleName(int roleId) throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    String role = DatabaseSelector.getRole(roleId, connection);
    connection.close();
    return role;
  }

  /**
   * get role associated with user.
   *
   * @param userId id of user
   * @return roleId the role's id
   * @throws SQLException data not in db
   */

  public static int getUserRoleId(int userId) throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int roleId = DatabaseSelector.getUserRole(userId, connection);
    connection.close();
    return roleId;
  }

  /**
   * Get users by role.
   */

  public static List<Integer> getUsersByRole(int roleId) throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getUsersByRole(roleId, connection);
    List<Integer> userIds = new ArrayList<>();
    while (results.next()) {
      if (results.getInt("ROLEID") == roleId) {
        userIds.get(results.getInt("USERIDS"));
      }
    }
    results.close();
    connection.close();
    return userIds;
  }

  /**
   * Get info on user.
   *
   * @return users list of users
   * @throws SQLException error in db
   */
  public static List<User> getUsersDetails() throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getUsersDetails(connection);
    List<User> users = new ArrayList<>();
    while (results.next()) {
//      users.add(UserFactory.buildUser(getUserRoleId(results.getInt("ID")), results.getInt("ID"),
//          results.getString("NAME"), results.getInt("AGE"), results.getString("ADDRESS")));
    }

    results.close();
    connection.close();
    return users;
  }

  /**
   * get users base on userid.
   *
   * @param userId id of user
   * @return user the user
   * @throws SQLException error in db
   * @throws UserNotFoundException User not in DB
   */
  public static User getUserDetails(int userId) throws SQLException, UserNotFoundException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getUserDetails(userId, connection);
    while (results.next()) {
      if (userId == results.getInt("ID")) {
//        User user =
//            (UserFactory.buildUser(getUserRoleId(results.getInt("ID")), results.getInt("ID"),
//                results.getString("NAME"), results.getInt("AGE"), results.getString("ADDRESS")));
        results.close();
        connection.close();
        return null;
      }
    }
    results.close();
    connection.close();
    throw new UserNotFoundException();
  }

  /**
   * Gets password of user from database.
   *
   * @param userId Id for target user
   * @return password Password of user associated with userId
   * @throws SQLException Throws if error in database.
   */
  public static String getPassword(int userId) throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    String password = DatabaseSelector.getPassword(userId, connection);
    connection.close();

    return password;
  }

  /**
   * Return list of all items in database.
   *
   * @return items List of items
   * @throws SQLException error in DB
   */
  public static List<Item> getAllItems() throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getAllItems(connection);
    List<Item> items = new ArrayList<>();
    while (results.next()) {
      items.add(new ItemImpl(results.getInt("ID"), results.getString("NAME"),
          new BigDecimal(results.getString("PRICE"))));
    }
    results.close();
    connection.close();
    return items;
  }

  /**
   * Returns item associated with Id.
   *
   * @param itemId Id for item
   * @return Item associated with id
   * @throws SQLException Error with database
   * @throws ItemNotFoundException Item not in DB
   */
  public static Item getItem(int itemId) throws ItemNotFoundException, SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getItem(itemId, connection);
    Item item;
    while (results.next()) {
      if (itemId == results.getInt("ID")) {
        if (map.containsKey(itemId)) {
          item = map.get(itemId);
        } else {
          item = new ItemImpl(itemId, results.getString("NAME"),
              new BigDecimal(results.getString("PRICE")));
          map.put(item.getId(), item);
        }
        results.close();
        connection.close();
        return item;
      }
    }
    results.close();
    connection.close();
    throw new ItemNotFoundException();
  }

  /**
   * Get inventory from the database.
   *
   * @return inventory Returns the inventory
   * @throws ItemNotFoundException Item not in DB.
   * @throws SQLException Error with database
   */
  public static Inventory getInventory() throws SQLException, ItemNotFoundException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getInventory(connection);
    int totalItems = 0;
    HashMap<Item, Integer> itemMap = new HashMap<Item, Integer>();

    while (results.next()) {
      itemMap.put(getItem(results.getInt("ITEMID")), results.getInt("QUANTITY"));
      totalItems++;
    }

    results.close();
    connection.close();
    return new InventoryBuilder().itemMap(itemMap).totalItems(totalItems).build();
  }

  /**
   * Return quantity of item.
   *
   * @param itemId The item
   * @return quantity of item
   * @throws SQLException Database output error
   */
  public static int getInventoryQuantity(int itemId) throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int quantity = DatabaseSelector.getInventoryQuantity(itemId, connection);
    connection.close();
    return quantity;
  }

  /**
   * Method returns all sales from database.
   *
   * @return log sales ledger
   * @throws SQLException Database error
   * @throws UserNotFoundException User not in DB
   * @throws SaleNotFoundException Sale not in DB
   * @throws ItemNotFoundException Item not in DB
   */
  public static SalesLog getSales()
      throws SQLException, UserNotFoundException, ItemNotFoundException, SaleNotFoundException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getSales(connection);

    SalesLog log = new SalesLogImpl();
    while (results.next()) {
      log.updateMap(results.getInt("ID"),
          new SaleImpl(results.getInt("ID"), getUserDetails(results.getInt("USERID")),
              new BigDecimal(results.getString("TOTALPRICE")),
              getItemizedSaleHashMap(results.getInt("ID"))));
    }
    results.close();
    connection.close();
    return log;
  }

  /**
   * Sale returned by id.
   *
   * @param saleId id of sale
   * @return sale
   * @throws SQLException error in db
   * @throws UserNotFoundException User not in DB
   * @throws SaleNotFoundException Sale not in DB
   * @throws ItemNotFoundException Item not in DB
   */
  public static Sale getSaleById(int saleId)
      throws SQLException, UserNotFoundException, SaleNotFoundException, ItemNotFoundException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getSaleById(saleId, connection);
    Sale sale;
    while (results.next()) {
      if (results.getInt("ID") == saleId) {
        sale = new SaleImpl(results.getInt("ID"), getUserDetails(results.getInt("USERID")),
            new BigDecimal(results.getString("TOTALPRICE")), getItemizedSaleHashMap(saleId));
        results.close();
        connection.close();
        return sale;
      }
    }

    results.close();
    connection.close();
    throw new SaleNotFoundException();
  }

  /**
   * Send list of sales.
   *
   * @param userId id of user.
   * @return sales list of sales
   * @throws SQLException error in db
   * @throws UserNotFoundException User not in DB
   * @throws SaleNotFoundException Sale not in DB
   * @throws ItemNotFoundException Item not in DB
   */
  public static List<Sale> getSalesToUser(int userId)
      throws SQLException, UserNotFoundException, ItemNotFoundException, SaleNotFoundException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelectHelper.getSalesToUser(userId, connection);
    List<Sale> sales = new ArrayList<>();
    while (results.next()) {
      if (userId == results.getInt("USERID")) {
        sales.add(new SaleImpl(results.getInt("ID"), getUserDetails(results.getInt("USERID")),
            new BigDecimal(results.getString("TOTALPRICE")),
            getItemizedSaleHashMap(results.getInt("ID"))));
      }
    }
    results.close();
    connection.close();
    return sales;
  }

  /**
   * Get sale with id.
   *
   * @param saleId id of sale
   * @return Sale targeted sale
   * @throws SQLException data not in db
   * @throws ItemNotFoundException Item not in DB
   * @throws SaleNotFoundException Sale not in DB
   * @throws UserNotFoundException User not in DB
   */

  public static Sale getItemizedSaleById(int saleId)
      throws SQLException, ItemNotFoundException, UserNotFoundException, SaleNotFoundException {
    return getSaleById(saleId);
  }

  /**
   * returns the hashmap of an itemized sale.
   *
   * @param saleId id of sale
   * @return hashmap containing items and quantity of sale
   * @throws SQLException error regarding sql database
   * @throws ItemNotFoundException error for wanted item not found
   * @throws UserNotFoundException error for wanted user not found
   * @throws SaleNotFoundException error for wanted sale not found
   */
  public static HashMap<Item, Integer> getItemizedSaleHashMap(int saleId)
      throws SQLException, ItemNotFoundException, UserNotFoundException, SaleNotFoundException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getItemizedSaleById(saleId, connection);
    HashMap<Item, Integer> salesMap = new HashMap<Item, Integer>();
    while (results.next()) {
      int tableId = results.getInt("SALEID");
      if (saleId == tableId) {
        int itemId = results.getInt("ITEMID");
        int quantity = results.getInt("QUANTITY");
        if (salesMap.containsKey(getItem(itemId))) {
          salesMap.replace(getItem(itemId), quantity);
        } else {
          salesMap.put(getItem(itemId), quantity);
        }
      }
    }
    results.close();
    connection.close();
    return salesMap;
  }


  /**
   * Method returns SalesLog for itemized sales.
   *
   * @return log SalesLog read from database
   * @throws SQLException Database error
   * @throws ItemNotFoundException Item not in DB
   * @throws SaleNotFoundException Sale not in DB
   * @throws UserNotFoundException User not in DB
   */
  public static SalesLog getItemizedSales()
      throws SQLException, ItemNotFoundException, UserNotFoundException, SaleNotFoundException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getItemizedSales(connection);
    SalesLog log = new SalesLogImpl();
    HashMap<Item, Integer> saleItems = new HashMap<Item, Integer>();
    while (results.next()) {

      saleItems.put(getItem(results.getInt("ITEMID")), results.getInt("QUANTITY"));

      log.updateMap(results.getInt("SALEID"),
          new SaleImpl(results.getInt("SALEID"), getSaleById(results.getInt("SALEID")).getUser(),
              (new BigDecimal(getItem(results.getInt("ITEMID")).getPrice().toString()).multiply(
                  new BigDecimal(new Integer(results.getInt("QUANTITY")).toString()))).setScale(2),
              saleItems));
    }
    results.close();
    connection.close();
    return log;
  }

  /**
   * returns a list of accounts from a given user.
   *
   * @param user user connected to accounts
   * @return list of accounts from user
   * @throws SQLException error regarding sql database
   * @throws InputException error regarding bad inputs
   * @throws ItemNotFoundException error for wanted item not found
   * @throws UserNotFoundException error for wanted user not found
   */
  public static List<Account> getAccounts(User user) throws SQLException, InputException,
      ItemNotFoundException, UserNotFoundException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getUserAccounts(user.getId(), connection);
    List<Account> accounts = new ArrayList<Account>();
    while (results.next()) {
      accounts.add(getAccountDetails(results.getInt("ID"), user));
    }

    results.close();
    connection.close();
    return accounts;
  }

  /**
   * returns a list of active accounts connected to user.
   *
   * @param user user connected to accounts
   * @return list of active accounts connected to user
   * @throws SQLException error regarding sql database
   * @throws InputException error for bad inputs
   * @throws ItemNotFoundException error for wanted item not found
   * @throws UserNotFoundException error for wanted user not found
   */
  public static List<Account> getAuthenticatedAccounts(User user)
      throws SQLException, InputException,
      ItemNotFoundException, UserNotFoundException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results;
    try {
      results = DatabaseSelector.getUserAccounts(user.getId(), connection);
    } catch (SQLException e) {
      return null;
    }
    List<Account> accounts = new ArrayList<Account>();
    while (results.next()) {
      accounts.add(getAuthenticatedAccountDetails(results.getInt("ID"), user));
    }

    results.close();
    connection.close();
    return accounts;
  }

  /**
   * Returns account object with account functionality.
   *
   * @param accountId id of the account
   * @param user User of the account
   * @return account object
   * @throws SQLException if database error
   * @throws InputException if input is invalid
   * @throws ItemNotFoundException error when item is not found
   */
  private static Account getAuthenticatedAccountDetails(int accountId, User user)
      throws SQLException, InputException, ItemNotFoundException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getAccountDetails(accountId, connection);
    Account account = null;
    ShoppingCart cart = null;
    Customer dummy = new Customer(user.getId(), user.getName(), user.getAge(), user.getAddress(),
        true);
    while (results.next()) {
      if (results.getInt("ACCTID") == accountId) {
        if (account == null) {
          cart = new ShoppingCart(dummy);
          account = new Account(accountId, cart);
          account.updateCart(getItem(results.getInt("ITEMID")), results.getInt("QUANTITY"));
        } else {
          account.updateCart(getItem(results.getInt("ITEMID")), results.getInt("QUANTITY"));
        }
      }
    }
    connection.close();
    results.close();
    if (account == null) {
      account = new Account(accountId, new ShoppingCart(dummy));
    }
    return account;
  }

  /**
   * Returns list of accounts from user who are currently active.
   *
   * @param user user of the account
   * @return list of integer id
   * @throws SQLException error in db
   * @throws InputException error in input
   * @throws ItemNotFoundException error when item is not found
   * @throws UserNotFoundException error when user is not found
   */
  public static List<Account> getUserActiveAccounts(User user) throws
      SQLException, InputException, ItemNotFoundException, UserNotFoundException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getUserActiveAccounts(user.getId(), connection);
    List<Account> activeAccounts = new ArrayList<Account>();
    while (results.next()) {
      activeAccounts.add(getAccountDetails(results.getInt("ID"), user));
    }

    results.close();
    connection.close();
    return activeAccounts;

  }

  /**
   * Return list of inactive user accounts.
   *
   * @param user user of acccounts
   * @return list of inactive user accounts
   * @throws SQLException error in db
   * @throws InputException error in input
   * @throws ItemNotFoundException error when item is not found
   * @throws UserNotFoundException error when user is not found
   */
  public static List<Account> getUserInactiveAccounts(User user)
      throws SQLException, InputException, ItemNotFoundException, UserNotFoundException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getUserInactiveAccounts(user.getId(), connection);
    List<Account> inactiveAccounts = new ArrayList<Account>();
    while (results.next()) {
      inactiveAccounts.add(getAccountDetails(results.getInt("ID"), user));
    }

    results.close();
    connection.close();
    return inactiveAccounts;
  }

  /**
   * Return full functioning account object.
   *
   * @param accountId id of account
   * @param user user of account
   * @return account object
   * @throws SQLException error in db
   * @throws InputException error in input
   * @throws ItemNotFoundException item not found in db
   * @throws UserNotFoundException user not found in db
   */
  public static Account getAccountDetails(int accountId, User user)
      throws SQLException, InputException, ItemNotFoundException, UserNotFoundException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getAccountDetails(accountId, connection);
    Account account = null;
    ShoppingCart cart = null;
    while (results.next()) {
      if (results.getInt("ACCTID") == accountId) {
        if (account == null) {
          cart = new ShoppingCart((Customer) user);
          account = new Account(accountId, cart);
          account.updateCart(getItem(results.getInt("ITEMID")), results.getInt("QUANTITY"));
        } else {
          account.updateCart(getItem(results.getInt("ITEMID")), results.getInt("QUANTITY"));
        }
      }
    }
    connection.close();
    results.close();
    if (account == null) {
      account = new Account(accountId, new ShoppingCart((Customer) user));
    }
    return account;
  }

  /**
   * Return list of inactive account ids.
   *
   * @param user User of the account
   * @return list of inactive account ids
   * @throws SQLException error in db
   * @throws InputException error in input
   * @throws ItemNotFoundException item not found in db
   * @throws UserNotFoundException user not found in db
   */
  public static List<Integer> getUserInactiveAccountIds(User user)
      throws SQLException, InputException, ItemNotFoundException, UserNotFoundException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getUserInactiveAccounts(user.getId(), connection);
    List<Integer> inactiveAccountIds = new ArrayList<Integer>();
    while (results.next()) {
      inactiveAccountIds.add(results.getInt("ID"));
    }
    results.close();
    connection.close();
    return inactiveAccountIds;
  }

  /**
   * return list of active account id of user.
   *
   * @param user user of account
   * @return list of active account id
   * @throws SQLException error in db
   * @throws InputException input is invalid
   * @throws ItemNotFoundException item not found in db
   * @throws UserNotFoundException user not found in db
   */
  public static List<Integer> getUserActiveAccountIds(User user)
      throws SQLException, InputException, ItemNotFoundException, UserNotFoundException {

    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getUserActiveAccounts(user.getId(), connection);
    List<Integer> inactiveAccountIds = new ArrayList<Integer>();
    while (results.next()) {
      inactiveAccountIds.add(results.getInt("ID"));
    }
    results.close();
    connection.close();
    return inactiveAccountIds;
  }

  /**
   * Get all account id from user.
   *
   * @param user user of account
   * @return list of account ids from user
   * @throws SQLException error in db
   * @throws InputException error in input
   * @throws ItemNotFoundException item not found in db
   * @throws UserNotFoundException user not found in db
   */
  public static List<Integer> getUserAccountIds(User user) throws SQLException,
      InputException, ItemNotFoundException, UserNotFoundException {
    List<Integer> userAccountIds = getUserActiveAccountIds(user);
    userAccountIds.addAll(getUserInactiveAccountIds(user));
    return userAccountIds;
  }
}
