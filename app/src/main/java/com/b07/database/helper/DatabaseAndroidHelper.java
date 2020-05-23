package com.b07.database.helper;

import android.content.Context;
import android.database.Cursor;
import com.b07.database.DatabaseDriverAndroid;
import com.b07.database.DatabaseInserter;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.ExceptionFactory;
import com.b07.exceptions.IncorrectActivityException;
import com.b07.exceptions.InputException;
import com.b07.exceptions.ItemNotFoundException;
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

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseAndroidHelper extends DatabaseDriverAndroid {

  public DatabaseAndroidHelper(Context context) {
    super(context);
  }
  //TODO add parameter checks to inserts and updates

  //Insert Methods
  public int insertRoleHelper(String role) {
    return Math.toIntExact(super.insertRole(role));
  }

  public int insertNewUserHelper(String name, int age, String address, String password)
      throws InputException {
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
    return Math.toIntExact(super.insertNewUser(name, age, address, password));
  }

  public int insertItemHelper(String name, BigDecimal price) throws InputException {
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
    return Math.toIntExact(super.insertItem(name, price));
  }

  public int insertUserRoleHelper(int userId, int roleId) {
    return Math.toIntExact(super.insertUserRole(userId, roleId));
  }

  public int insertInventoryHelper(int itemId, int quantity) throws InputException {
    if (quantity < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    return Math.toIntExact(super.insertInventory(itemId, quantity));
  }

  public int insertSaleHelper(int userId, BigDecimal totalPrice) throws InputException {
    if (totalPrice == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (totalPrice.floatValue() < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    } else if (totalPrice.scale() != 2) {
      throw ExceptionFactory.createException("PrecisionException");
    }
    return Math.toIntExact(super.insertSale(userId, totalPrice));
  }

  public int insertRefundHelper(int userId, BigDecimal totalPrice) throws InputException {
    if (totalPrice == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (totalPrice.scale() != 2) {
      throw ExceptionFactory.createException("PrecisionException");
    }
    //TODO fix refund
    return Math.toIntExact(insertSale(userId, totalPrice));
  }

  private ArrayList<Integer> combinations = new ArrayList<>();

  public int insertItemizedSaleHelper(int saleId, int itemId, int quantity) throws InputException {
    if (quantity < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    } else if (combinations.contains(new Integer(saleId + itemId))) {
      throw ExceptionFactory.createException("InvalidCombinationException");
    }
    return Math.toIntExact(super.insertItemizedSale(saleId, itemId, quantity));
  }

  public int insertItemizedRefundHelper(int saleId, int itemId, int quantity)
      throws InputException {
    if (combinations.contains(new Integer(saleId + itemId))) {
      throw ExceptionFactory.createException("InvalidCombinationException");
    }
    //TODO fix refund_view
    return Math.toIntExact(insertItemizedSale(saleId, itemId, quantity));
  }

  public int insertAccountHelper(int userId, boolean active, Context context)
      throws UserNotFoundException, InputException {
    getUserDetailsHelper(userId, context);
    if (!getRoleHelper(userId)
        .equals(Roles.CUSTOMER.toString())) {
      throw ExceptionFactory.createException("InvalidRoleException");
    }
    return Math.toIntExact(super.insertAccount(userId, active));
  }

  public int insertAccountLineHelper(int accountId, int itemId, int quantity)
      throws InputException {
    if (quantity < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    return Math.toIntExact(super.insertAccountLine(accountId, itemId, quantity));
  }

  //Select Methods
  public List<Integer> getRolesHelper() {
    Cursor cursor = getRoles();
    ArrayList<Integer> roles = new ArrayList();
    while (cursor.moveToNext()) {
      roles.add(cursor.getInt(cursor.getColumnIndex("ID")));
    }
    cursor.close();
    return roles;
  }

  public String getRoleHelper(int id) {
    return super.getRole(id);
  }

  public int getUserRoleHelper(int userId) {
    return super.getUserRole(userId);
  }

  public List<Integer> getUsersByRoleHelper(int roleId) {
    Cursor cursor = getUsersByRole(roleId);
    ArrayList<Integer> roles = new ArrayList();
    while (cursor.moveToNext()) {
      if (cursor.getInt(cursor.getColumnIndex("ROLEID")) == roleId) {
        roles.add(cursor.getInt(cursor.getColumnIndex("USERIDS")));
      }
    }
    cursor.close();
    return roles;
  }

  public List<User> getUsersDetailsHelper(Context context) {
    Cursor cursor = super.getUsersDetails();
    List<User> users = new ArrayList<>();
    while (cursor.moveToNext()) {
      int id = cursor.getInt(cursor.getColumnIndex("ID"));
      String name = cursor.getString(cursor.getColumnIndex("NAME"));
      int age = cursor.getInt(cursor.getColumnIndex("AGE"));
      String address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
      users.add(UserFactory.buildUser(getUserRoleHelper(id), id, name, age, address, context));
    }
    cursor.close();
    return users;
  }

  public User getUserDetailsHelper(int userId, Context context) throws UserNotFoundException {
    Cursor cursor = super.getUserDetails(userId);
    User user = null;
    while (cursor.moveToNext()) {
      if (userId == cursor.getInt(cursor.getColumnIndex("ID"))) {
        String name = cursor.getString(cursor.getColumnIndex("NAME"));
        int age = cursor.getInt(cursor.getColumnIndex("AGE"));
        String address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
        System.out.println(name + " " + age + " " + address);
        user = UserFactory.buildUser(getUserRoleHelper(userId), userId, name, age, address, context);
        cursor.close();
        return user;
      }
    }
    cursor.close();
    throw new UserNotFoundException();
  }

  public String getPasswordHelper(int userId) {
    return super.getPassword(userId);
  }

  public List<Item> getAllItemsHelper() {
    Cursor cursor = getAllItems();
    List<Item> items = new ArrayList<>();
    while (cursor.moveToNext()) {
      int id = cursor.getInt(cursor.getColumnIndex("ID"));
      String name = cursor.getString(cursor.getColumnIndex("NAME"));
      BigDecimal price = new BigDecimal(cursor.getString(cursor.getColumnIndex("PRICE")));
      items.add(new ItemImpl(id, name, price));
    }
    cursor.close();
    return items;
  }

//  private HashMap<Integer, Item> map = new HashMap<Integer, Item>();

  public Item getItemHelper(int itemId) throws ItemNotFoundException {
    Cursor cursor = getItem(itemId);
    Item item;
    while (cursor.moveToNext()) {
      if (itemId == cursor.getInt(cursor.getColumnIndex("ID"))) {


          item = new ItemImpl(itemId, cursor.getString(cursor.getColumnIndex("NAME")),
              new BigDecimal(cursor.getString(cursor.getColumnIndex("PRICE"))));

        cursor.close();
        return item;
      }
    }
    cursor.close();
    throw new ItemNotFoundException();
  }

  public Inventory getInventoryHelper() throws ItemNotFoundException {
    Cursor cursor = getInventory();
    int totalItems = 0;
    HashMap<Item, Integer> itemMap = new HashMap<Item, Integer>();

    while (cursor.moveToNext()) {
      itemMap.put(getItemHelper(cursor.getInt(cursor.getColumnIndex("ITEMID"))),
          cursor.getInt(cursor.getColumnIndex("QUANTITY")));
      totalItems++;
    }

    cursor.close();
    return new InventoryBuilder().itemMap(itemMap).totalItems(totalItems).build();
  }

  public int getInventoryQuantityHelper(int itemId) {
    return super.getInventoryQuantity(itemId);
  }

  public SalesLog getSalesHelper(Context context) throws ItemNotFoundException, UserNotFoundException {
    Cursor cursor = getSales();

    SalesLog log = new SalesLogImpl();
    while (cursor.moveToNext()) {
      log.updateMap(cursor.getInt(cursor.getColumnIndex("ID")),
          new SaleImpl(cursor.getInt(cursor.getColumnIndex("ID")),
              getUserDetailsHelper(cursor.getInt(cursor.getColumnIndex("USERID")), context),
              new BigDecimal(cursor.getString(cursor.getColumnIndex("TOTALPRICE"))),
              getItemizedSaleHashMapHelper(cursor.getInt(cursor.getColumnIndex("ID")))));
    }
    cursor.close();
    return log;
  }

  public Sale getSaleByIdHelper(int saleId, Context context)
      throws UserNotFoundException, ItemNotFoundException, SaleNotFoundException {
    Cursor cursor = getSaleById(saleId);
    Sale sale;
    while (cursor.moveToNext()) {
      if (cursor.getInt(cursor.getColumnIndex("ID")) == saleId) {
        sale = new SaleImpl(saleId,
            getUserDetailsHelper(cursor.getInt(cursor.getColumnIndex("USERID")), context),
            new BigDecimal(cursor.getString(cursor.getColumnIndex("TOTALPRICE"))),
            getItemizedSaleHashMapHelper(saleId));
        cursor.close();
        return sale;
      }
    }
    cursor.close();
    throw new SaleNotFoundException();
  }

  public List<Sale> getSalesToUserHelper(int userId, Context context)
      throws UserNotFoundException, ItemNotFoundException, SaleNotFoundException {
    Cursor cursor = getSalesToUser(userId);
    List<Sale> sales = new ArrayList<>();
    while (cursor.moveToNext()) {
      if (userId == cursor.getInt(cursor.getColumnIndex("USERID"))) {
        sales.add(
            new SaleImpl(cursor.getInt(cursor.getColumnIndex("ID")), getUserDetailsHelper(userId, context),
                new BigDecimal(cursor.getString(cursor.getColumnIndex("TOTALPRICE"))),
                getItemizedSaleHashMapHelper(cursor.getInt(cursor.getColumnIndex("ID")))));
      }
    }
    cursor.close();
    return sales;
  }

  public SalesLog getItemizedSalesHelper(Context context)
      throws ItemNotFoundException, UserNotFoundException, SaleNotFoundException {
    Cursor cursor = getItemizedSales();
    SalesLog log = new SalesLogImpl();
    HashMap<Item, Integer> saleItems = new HashMap<Item, Integer>();
    while (cursor.moveToNext()) {
      saleItems.put(getItemHelper(cursor.getInt(cursor.getColumnIndex("ITEMID"))),
          cursor.getInt(cursor.getColumnIndex("QUANTITY")));

      log.updateMap(cursor.getInt(cursor.getColumnIndex("SALEID")),
          new SaleImpl(cursor.getInt(cursor.getColumnIndex("SALEID")),
              getSaleByIdHelper(cursor.getInt(cursor.getColumnIndex("SALEID")), context).getUser(),
              (new BigDecimal(
                  getItemHelper(cursor.getInt(cursor.getColumnIndex("ITEMID"))).getPrice()
                      .toString()).multiply(
                  new BigDecimal(
                      new Integer(cursor.getInt(cursor.getColumnIndex("QUANTITY"))).toString())))
                  .setScale(2),
              saleItems));
    }
    cursor.close();
    return log;
  }

  public Sale getItemizedSaleByIdHelper(int saleId, Context context)
      throws ItemNotFoundException, UserNotFoundException, SaleNotFoundException {
    return getSaleByIdHelper(saleId, context);
  }

  public HashMap<Item, Integer> getItemizedSaleHashMapHelper(int saleId)
      throws ItemNotFoundException {
    Cursor cursor = getItemizedSaleById(saleId);
    HashMap<Item, Integer> salesMap = new HashMap<Item, Integer>();
    while (cursor.moveToNext()) {
      int tableId = cursor.getInt(cursor.getColumnIndex("SALEID"));
      if (saleId == tableId) {
        int itemId = cursor.getInt(cursor.getColumnIndex("ITEMID"));
        int quantity = cursor.getInt(cursor.getColumnIndex("QUANTITY"));
        if (salesMap.containsKey(getItem(itemId))) {
          salesMap.replace(getItemHelper(itemId), quantity);
        } else {
          salesMap.put(getItemHelper(itemId), quantity);
        }
      }
    }
    cursor.close();
    return salesMap;
  }

  public List<Account> getUserAccountsHelper(User user) throws InputException,
      ItemNotFoundException {
    Cursor cursor = getUserAccounts(user.getId());
    List<Account> accounts = new ArrayList<Account>();
    while (cursor.moveToNext()) {
      accounts.add(getAccountDetailsHelper(cursor.getInt(cursor.getColumnIndex("ID")), user));
    }
    cursor.close();
    return accounts;
  }

  public List<Account> getAuthenticatedAccountsHelper(User user)
      throws InputException,
      ItemNotFoundException {

    Cursor cursor = getUserAccounts(user.getId());
    List<Account> accounts = new ArrayList<Account>();
    while (cursor.moveToNext()) {
      accounts
          .add(getAuthenticatedAccountDetailsHelper(cursor.getInt(cursor.getColumnIndex("ID")),
              user));
    }
    cursor.close();
    return accounts;
  }

  private Account getAuthenticatedAccountDetailsHelper(int accountId, User user)
      throws InputException, ItemNotFoundException {
    Cursor cursor = getAccountDetails(accountId);
    Account account = null;
    ShoppingCart cart = null;
    Customer dummy = new Customer(user.getId(), user.getName(), user.getAge(), user.getAddress(),
        true);
    while (cursor.moveToNext()) {
      if (cursor.getInt(cursor.getColumnIndex("ACCTID")) == accountId) {
        if (account == null) {
          cart = new ShoppingCart(dummy);
          account = new Account(accountId, cart);
          account.updateCart(getItemHelper(cursor.getInt(cursor.getColumnIndex("ITEMID"))),
              cursor.getInt(cursor.getColumnIndex("QUANTITY")));
        } else {
          account.updateCart(getItemHelper(cursor.getInt(cursor.getColumnIndex("ITEMID"))),
              cursor.getInt(cursor.getColumnIndex("QUANTITY")));
        }
      }
    }
    cursor.close();
    if (account == null) {
      account = new Account(accountId, new ShoppingCart(dummy));
    }
    return account;
  }

  public List<Account> getUserActiveAccountsHelper(User user)
      throws InputException, ItemNotFoundException {
    Cursor cursor = getUserActiveAccounts(user.getId());
    List<Account> activeAccounts = new ArrayList<Account>();
    while (cursor.moveToNext()) {
      activeAccounts.add(getAccountDetailsHelper(cursor.getInt(cursor.getColumnIndex("ID")), user));
    }
    cursor.close();
    return activeAccounts;
  }

  public List<Account> getUserInactiveAccountsHelper(User user)
      throws InputException, ItemNotFoundException {
    Cursor cursor = getUserInactiveAccounts(user.getId());
    List<Account> inactiveAccounts = new ArrayList<Account>();
    while (cursor.moveToNext()) {
      inactiveAccounts
          .add(getAccountDetailsHelper(cursor.getInt(cursor.getColumnIndex("ID")), user));
    }
    cursor.close();
    return inactiveAccounts;
  }

  public Account getAccountDetailsHelper(int accountId, User user)
      throws InputException, ItemNotFoundException {
    Cursor cursor = getAccountDetails(accountId);
    Account account = null;
    ShoppingCart cart = null;
    while (cursor.moveToNext()) {
      if (cursor.getInt(cursor.getColumnIndex("ACCTID")) == accountId) {
        if (account == null) {
          cart = new ShoppingCart((Customer) user);
          account = new Account(accountId, cart);
          account.updateCart(getItemHelper(cursor.getInt(cursor.getColumnIndex("ITEMID"))),
              cursor.getInt(cursor.getColumnIndex("QUANTITY")));
        } else {
          account.updateCart(getItemHelper(cursor.getInt(cursor.getColumnIndex("ITEMID"))),
              cursor.getInt(cursor.getColumnIndex("QUANTITY")));
        }
      }
    }
    cursor.close();
    if (account == null) {
      account = new Account(accountId, new ShoppingCart((Customer) user));
    }
    account.setCustomer((Customer)user);
    return account;
  }

  public List<Integer> getUserInactiveAccountIdsHelper(User user) {
    Cursor cursor = getUserInactiveAccounts(user.getId());
    List<Integer> inactiveAccountIds = new ArrayList<Integer>();
    while (cursor.moveToNext()) {
      inactiveAccountIds.add(cursor.getInt(cursor.getColumnIndex(("ID"))));
    }
    cursor.close();
    return inactiveAccountIds;
  }

  public List<Integer> getUserActiveAccountIdsHelper(User user) {
    Cursor cursor = getUserActiveAccounts(user.getId());
    List<Integer> inactiveAccountIds = new ArrayList<Integer>();
    while (cursor.moveToNext()) {
      inactiveAccountIds.add(cursor.getInt(cursor.getColumnIndex("ID")));
    }
    cursor.close();
    return inactiveAccountIds;
  }

  public List<Integer> getUserAccountIdsHelper(User user) {
    List<Integer> userAccountIds = getUserActiveAccountIdsHelper(user);
    userAccountIds.addAll(getUserInactiveAccountIdsHelper(user));
    return userAccountIds;
  }

  //Update Methods
  public boolean updateRoleNameHelper(String name, int id) throws InputException {
    if (name == null) {
      throw ExceptionFactory.createException("NullInputException");
    }
    if (Roles.ADMIN.toString().equals(name) && Roles.CUSTOMER.toString().equals(name)
        && Roles.EMPLOYEE.toString().equals(name)) {
      throw ExceptionFactory.createException("InvalidRoleException");
    }
    return super.updateRoleName(name, id);
  }

  public boolean updateUserNameHelper(String name, int id) throws InputException {
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
    return super.updateUserName(name, id);
  }

  public boolean updateUserAgeHelper(int age, int id) throws InputException {
    if (age < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    return super.updateUserAge(age, id);
  }

  public boolean updateUserAddressHelper(String address, int id) throws InputException {
    if (address == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (address.length() > 100) {
      throw ExceptionFactory.createException("StringLengthException");
    } else if (!address.matches("^[a-zA-Z0-9]*$")) {
      throw ExceptionFactory.createException("InvalidNameException");
    }
    return super.updateUserAddress(address, id);
  }

  public boolean updateUserRoleHelper(int roleId, int id) {
    return super.updateUserRole(roleId, id);
  }

  public boolean updateItemNameHelper(String name, int id) throws InputException {
    if (name == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (name.length() > 63) {
      throw ExceptionFactory.createException("StringLengthException");
    } else if (!name.matches("^[a-zA-Z]*$")) {
      throw ExceptionFactory.createException("InvalidNameException");
    }
    return super.updateItemName(name, id);
  }

  public boolean updateItemPriceHelper(BigDecimal price, int id) throws InputException {
    if (price == null) {
      throw ExceptionFactory.createException("NullInputException");
    } else if (price.scale() != 2) {
      throw ExceptionFactory.createException("PrecisionException");
    } else if (price.floatValue() < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    return super.updateItemPrice(price, id);
  }

  public boolean updateInventoryQuantityHelper(int quantity, int id) throws InputException {
    if (quantity < 0) {
      throw ExceptionFactory.createException("NegativeQuantityException");
    }
    return super.updateInventoryQuantity(quantity, id);
  }

  public boolean updateAccountStatusHelper(int accountId, boolean active)
      throws IncorrectActivityException {
    if (active) {
      throw new IncorrectActivityException();
    }
    return super.updateAccountStatus(accountId, active);
  }

  //misc
  private static String removeSpace(String name) {
    String temp = "";
    for (String s : name.split(" ")) {
      temp += s;
    }
    return temp;
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
  public int portNewUserHelper(String name, int age, String address, String password)
          throws SQLException, DatabaseInsertException, InputException {

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
    return Math.toIntExact(super.portNewUser(name, age, address, password));
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
  public int portAccountHelper(int userId, boolean active, Context context)
          throws UserNotFoundException, InputException {
    getUserDetailsHelper(userId, context);
    if (!getRoleHelper(userId)
            .equals(Roles.CUSTOMER.toString())) {
      throw ExceptionFactory.createException("InvalidRoleException");
    }
    return Math.toIntExact(super.insertAccount(userId, active));

  }

//  public void reInitalize(Context context){
//    File file = new File();
//    file.delete();
//  }
}
