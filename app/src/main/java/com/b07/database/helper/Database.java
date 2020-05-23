package com.b07.database.helper;

import com.b07.exceptions.ConnectionFailedException;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.ExceptionHandler;
import com.b07.exceptions.InputException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Item;
import com.b07.store.Sale;
import com.b07.store.SalesLog;
import com.b07.store.Store;
import com.b07.users.Account;
import com.b07.users.User;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

  private static String path = "SerializedDatabase.txt";

  /**
   * Get data from DB and store it into object. Then serialize object
   *
   * @return name of object
   * @throws SQLException error regarding sql database
   * @throws InputException error in input
   */
  public static String backupDatabase() throws SQLException, InputException {

    Store store = new Store();
    try {

      HashMap<Integer, String> password = new HashMap<Integer, String>();
      HashMap<Integer, String> roles = new HashMap<Integer, String>();
      HashMap<Integer, Integer> roleId = new HashMap<Integer, Integer>();
      HashMap<Integer, List<Account>> accounts = new HashMap<Integer, List<Account>>();
      HashMap<Integer, Boolean> activeAccounts = new HashMap<Integer, Boolean>();

      for (User user : DatabaseSelectHelper.getUsersDetails()) {
        password.put(user.getId(), DatabaseSelectHelper.getPassword(user.getId()));
        roleId.put(user.getId(), user.getRoleId(null));
        List<Account> temp = DatabaseSelectHelper.getAuthenticatedAccounts(user);
        if (temp != null) {
          accounts.put(user.getId(), temp);
          List<Integer> accountIds = DatabaseSelectHelper.getUserActiveAccountIds(user);
          for (Integer accountId : accountIds) {
            activeAccounts.put(accountId, true);
          }
          accountIds = DatabaseSelectHelper.getUserInactiveAccountIds(user);
          for (Integer accountId : accountIds) {
            activeAccounts.put(accountId, false);
          }
        }
      }
      for (Integer id : DatabaseSelectHelper.getRoleIds()) {
        roles.put(id, DatabaseSelectHelper.getRoleName(id));
      }
      store.setUsers((ArrayList<User>) DatabaseSelectHelper.getUsersDetails());
      store.setInventory(DatabaseSelectHelper.getInventory());
      store.setItems((ArrayList<Item>) DatabaseSelectHelper.getAllItems());
      store.setLog(DatabaseSelectHelper.getItemizedSales());
      store.setSalesLog(DatabaseSelectHelper.getSales().getFullSale());
      store.setPassword(password);
      store.setRoleIds(roleId);
      store.setRoles(roles);
      store.setAccounts(accounts);
      store.setAccountsActive(activeAccounts);

    } catch (SQLException e) {
      ExceptionHandler.handle(e);
    } catch (ItemNotFoundException e) {
      ExceptionHandler.handle(e);
    } catch (UserNotFoundException e) {
      ExceptionHandler.handle(e);
    } catch (SaleNotFoundException e) {
      ExceptionHandler.handle(e);
    }

    return serialize(store);

  }

  /**
   * Restores DB.
   */
  public static void restoreDatabase() {
    String fileName = "com.b07.store.Store@2dda6444";
    Store backup = (Store) deserialize(fileName);
    try {
      testDeserialize(backup);
      newDatabase(backup);
    } catch (ConnectionFailedException | DatabaseInsertException | SQLException
        | ItemNotFoundException | InputException | SaleNotFoundException
        | UserNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Take deserialized object and prints out content to test.
   *
   * @param backup backup of store object
   * @throws SQLException error regarding sql database
   */
  private static void testDeserialize(Store backup) throws SQLException {
    // System.out.println(backup);
    // System.out.println(backup == null);
    System.out.println("Inventory of DB");
    for (Item item : backup.getInventory().getItemMap().keySet()) {
      System.out.println("Id: " + item.getId() + " Item name: " + item.getName() + " Quantity: "
          + backup.getInventory().getItemMap().get(item));
    }
    System.out.println("Users of DB");
    for (User user : backup.getUsers()) {
      System.out.println("ID: " + user.getId() + " Name: " + user.getName() + "Role: "
          + DatabaseSelectHelper.getRoleName(user.getRoleId(null)) + " " + user.getRoleId(null));
    }
  }

  /**
   * Helper function inserts data from serialized file into DB.
   *
   * @param backup backup of store object
   * @throws ConnectionFailedException thrown for failure to connect to database
   * @throws DatabaseInsertException thrown for error inserting into database
   * @throws SQLException error regarding sql database
   * @throws ItemNotFoundException error for when wanted item is not found
   * @throws InputException error regarding bad inputs
   * @throws SaleNotFoundException error for when wanted sale is not found
   * @throws UserNotFoundException error for when wanted user is not found
   */
  private static void newDatabase(Store backup)
      throws ConnectionFailedException, DatabaseInsertException, SQLException,
      ItemNotFoundException, InputException, SaleNotFoundException, UserNotFoundException {

    Connection connection = DatabaseDriverHelper.reInitializeDatabase();

    if (connection == null) {
      System.out.println("NOOOOOOO!");
      return;
    }

    if (backup == null) {
      connection.close();
      return;
    }

    if (backup.getItems() != null) {
      HashMap<Integer, Item> itemMap = getItemMap(backup.getItems());
      for (int i = 1; i <= itemMap.size(); i++) {
        Item item = itemMap.get(i);
        DatabaseInsertHelper.insertItem(item.getName(), item.getPrice());
      }
    }

    if (backup.getRoles() != null) {
      for (int i = 1; i <= backup.getRoles().size(); i++) {
        DatabaseInsertHelper.insertRole(backup.getRoles().get(i));
      }
    }

    if (backup.getUsers() != null) {
      HashMap<Integer, User> userMap = getUserMap(backup.getUsers());
      for (int i = 1; i <= userMap.size(); i++) {
        User user = userMap.get(i);
        DatabaseInsertHelper.portNewUser(user.getName(), user.getAge(), user.getAddress(),
            backup.getPassword().get(user.getId()));

        DatabaseInsertHelper.insertUserRole(i, backup.getRoleIds().get(i));
      }
    }

    if (backup.getInventory() != null && backup.getInventory().getItemMap() != null) {
      for (Item item : backup.getInventory().getItemMap().keySet()) {
        DatabaseInsertHelper.insertInventory(item.getId(),
            backup.getInventory().getItemMap().get(item));
      }
    }

    if (backup.getLog() != null) {
      HashMap<Integer, List<Sale>> sales = getItemizedSaleMap(backup.getLog());
      for (int i = 1; i <= sales.size(); i++) {
        for (Sale sale : sales.get(i)) {
          for (Item item : sale.getItemMap().keySet()) {
            DatabaseInsertHelper.insertItemizedRefund(i, item.getId(), sale.getItemMap().get(item));
          }
        }
      }
    }

    if (backup.getFullSale() != null) {
      for (int i = 1; i <= backup.getFullSale().size(); i++) {
        Sale sale = backup.getFullSale().get(i);
        DatabaseInsertHelper.insertRefund(sale.getUser().getId(), sale.getTotalPrice());
      }
    }

    if (backup.getAccounts() != null) {
      HashMap<Integer, Integer> accountMap = getAccountMap(backup.getAccounts());
      for (int i = 1; i <= accountMap.size(); i++) {
        DatabaseInsertHelper.portAccount(accountMap.get(i), backup.getAccountsActive().get(i));
      }

      for (Integer userId : backup.getAccounts().keySet()) {
        for (Account account : backup.getAccounts().get(userId)) {
          for (Item item : account.getCart().getMap().keySet()) {
            DatabaseInsertHelper.insertAccountLine(account.getId(), item.getId(),
                account.getCart().getMap().get(item));
          }
        }
      }
    }
    connection.close();
  }



  private static HashMap<Integer, Integer> getAccountMap(HashMap<Integer, List<Account>> map) {
    HashMap<Integer, Integer> accountUser = new HashMap<Integer, Integer>();
    for (Integer userId : map.keySet()) {
      for (Account account : map.get(userId)) {
        accountUser.put(account.getId(), userId);
      }
    }
    return accountUser;
  }

  /**
   * Helper for itemized Sale.
   *
   * @param log sales log to get map
   * @return itemized sales of the log
   */
  private static HashMap<Integer, List<Sale>> getItemizedSaleMap(SalesLog log) {
    HashMap<Integer, List<Sale>> itemizedSales = new HashMap<Integer, List<Sale>>();
    for (Integer userId : log.getLogMap().keySet()) {
      List<Sale> sales = log.getLogMap().get(userId);
      for (Sale sale : sales) {
        if (itemizedSales.containsKey(sale.getId())) {
          itemizedSales.get(sale.getId()).add(sale);
        } else {
          List<Sale> temp = new ArrayList<Sale>();
          temp.add(sale);
          itemizedSales.put(sale.getId(), temp);
        }
      }
    }
    return itemizedSales;
  }


  /**
   * Helper for items.
   *
   * @param items items to map
   * @return map of items
   */
  private static HashMap<Integer, Item> getItemMap(List<Item> items) {
    HashMap<Integer, Item> map = new HashMap<Integer, Item>();

    for (Item item : items) {
      map.put(item.getId(), item);
    }

    return map;
  }

  private static HashMap<Integer, User> getUserMap(List<User> users) {
    HashMap<Integer, User> map = new HashMap<Integer, User>();
    for (User user : users) {
      map.put(user.getId(), user);
    }
    return map;
  }

  /**
   * Joe's code LUL.
   *
   * @param x thing to serialize
   * @return serialized item string
   */
  private static String serialize(Serializable x) {
    try {
      FileOutputStream fileOut = new FileOutputStream(path);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(x);
      out.close();
      fileOut.close();
      System.out.println("Serialized data is saved in " + path);
    } catch (IOException i) {
      i.printStackTrace();
    }
    return x.toString();
  }

  /**
   * Joe's code.
   *
   * @param fileName name of serialized file
   * @return
   */
  private static Object deserialize(String fileName) {
    try {
      FileInputStream fileIn = new FileInputStream(path);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      Object z = in.readObject();
      in.close();
      fileIn.close();
      return z;
    } catch (IOException i) {
      i.printStackTrace();
      return null;
    } catch (ClassNotFoundException c) {
      System.out.println("Cat class not found");
      c.printStackTrace();
      return null;
    }
  }
}
