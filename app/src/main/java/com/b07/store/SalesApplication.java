package com.b07.store;

import com.b07.database.helper.Database;
import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.database.helper.DatabaseUpdateHelper;
import com.b07.exceptions.AccountNotFoundException;
import com.b07.exceptions.AuthenticationException;
import com.b07.exceptions.ConnectionFailedException;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.ExceptionHandler;
import com.b07.exceptions.InputException;
import com.b07.exceptions.InsufficientInventoryException;
import com.b07.exceptions.InvalidCredentialsException;
import com.b07.exceptions.InvalidNameException;
import com.b07.exceptions.InvalidRoleException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.NegativeQuantityException;
import com.b07.exceptions.NullInputException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.StringLengthException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Inventory;
import com.b07.inventory.Item;
import com.b07.inventory.ItemEnum;
import com.b07.users.Account;
import com.b07.users.Admin;
import com.b07.users.Employee;
import com.b07.users.EmployeeInterface;
import com.b07.users.Roles;
import com.b07.users.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// make sure back option works
// catch new authentication

public class SalesApplication {
  private static final BigDecimal TAXRATE = new BigDecimal("1.13");
  
  /**
   * This is the main method to run the entire program. Takes input from the user to take them to
   * the correct place to interact with the database.
   * 
   * @param argv initializes database if -1, directs them to admin mode if 1.
   */
  public static void main(String[] argv) {

    try {
      int arg = 4;
      if (argv != null && argv.length > 0) {
        arg = parseInput(argv[0], 4);
      }
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      if (arg == -1) {
        try {
          Connection connection = DatabaseDriverExtender.connectOrCreateDataBase();
          if (connection == null) {
            System.out.println("NOOO");
          }
          DatabaseDriverExtender.initialize(connection);
          firstRunOnlyInit(connection);
          connection.close();
        } catch (Exception e) {
          ExceptionHandler.handle(e);
        }
      } else if (arg == 1) {
        adminMode(bufferedReader);
      } else if (arg == 30) {
        System.out.println("Reloading Database");
        Database.restoreDatabase();
      } else {
        try {
          List<User> list = DatabaseSelectHelper.getUsersDetails();
          for (User user : list) {
            System.out.println(user.getName() + " " + user.getId());
          }
          menuPrompt(bufferedReader);
        } catch (InputException e) {
          ExceptionHandler.handle(e);
        }
      }
      bufferedReader.close();
      System.out.println("Goodbye.");
    } catch (IOException error) {
      error.printStackTrace();
    } catch (Exception error) {
      ExceptionHandler.handle(error);
    }
  }

  /**
   * This is for the first run only! Add this code: DatabaseDriverExtender.initialize(connection);
   * Then add code to create your first account, an administrator with a password Once this is done,
   * create an employee account as well.
   * 
   * @param connection connection to database
   * @throws SQLException thrown for errors accessing database
   * @throws ConnectionFailedException thrown when connection to database failed
   * @throws DatabaseInsertException thrown on failure when inserting data
   * @throws NullInputException thrown when input is null when expecting a non-null input
   * @throws InvalidNameException thrown when the name given is not valid
   * @throws NegativeQuantityException thrown when a negative quantity is given when a non-negative
   *         value is expected
   * @throws StringLengthException thrown when the length of a string given does not fit what is
   *         expected
   */
  private static void firstRunOnlyInit(Connection connection)
      throws SQLException, ConnectionFailedException, DatabaseInsertException, NullInputException,
      InvalidNameException, NegativeQuantityException, StringLengthException {

    int adminId = -1;
    int employeeId = -1;

    try {
      adminId = DatabaseInsertHelper.insertNewUser("admin", 70, "Oakland ", "password");
      employeeId = DatabaseInsertHelper.insertNewUser("george", 8, "12 Monkey land", "password");
    } catch (InputException error) {
      ExceptionHandler.handle(error);
    }
    int roleId = DatabaseInsertHelper.insertRole(Roles.ADMIN.toString());
    DatabaseInsertHelper.insertUserRole(adminId, roleId);
    int employeeroleId = DatabaseInsertHelper.insertRole(Roles.EMPLOYEE.toString());
    DatabaseInsertHelper.insertUserRole(employeeId, employeeroleId);

    try {
      int id1 =
          DatabaseInsertHelper.insertItem(ItemEnum.FISHING_ROD.toString(), new BigDecimal("5.57"));
      int id2 = DatabaseInsertHelper.insertItem(ItemEnum.HOCKEY_STICK.toString(),
          new BigDecimal("29.99"));
      int id3 =
          DatabaseInsertHelper.insertItem(ItemEnum.PROTEIN_BAR.toString(), new BigDecimal("99.99"));
      int id4 = DatabaseInsertHelper.insertItem(ItemEnum.RUNNING_SHOES.toString(),
          new BigDecimal("0.99"));
      int id5 =
          DatabaseInsertHelper.insertItem(ItemEnum.SKATES.toString(), new BigDecimal("129.99"));

      DatabaseInsertHelper.insertInventory(id1, 18);
      DatabaseInsertHelper.insertInventory(id2, 184);
      DatabaseInsertHelper.insertInventory(id3, 1);
      DatabaseInsertHelper.insertInventory(id4, 18);
      DatabaseInsertHelper.insertInventory(id5, 122);

    } catch (Exception e) {
      ExceptionHandler.handle(e);
    }
    System.out.println("Database Initialized");
  }

  /**
   * In admin mode, the user must first login with a valid admin account. This will allow the user
   * to promote employees to admins. Currently, this is all an admin can do.
   * 
   * @param bufferedReader read input from user
   */
  private static void adminMode(BufferedReader bufferedReader) {

    User user;
    try {
      try {
        user = authenticateUser(bufferedReader, Roles.ADMIN);
      } catch (Exception e) {
        ExceptionHandler.handle(e);
        adminMode(bufferedReader);
        return;
      }
      int input;
      do {
        System.out.println("1. Promote Employee 2. Read Books "
            + "3. Get Historic Accounts 4. Get Active Accounts 5. Get All Accounts "
            + "6. Backup DB 0. Exit");
        input = getIntegerInput(bufferedReader);
        if (input == 1) {
          printEmployeeList();
          System.out.println("Enter the id of the Employee to Promote:");

          try { // check and then attempt to promote
            User employee = DatabaseSelectHelper.getUserDetails(getIntegerInput(bufferedReader));
            if (!DatabaseSelectHelper.getRoleName(employee.getRoleId()).toString()
                .equals(Roles.EMPLOYEE.toString())) {
              throw new InvalidRoleException();
            }
            if (((Admin) user).promoteEmployee((Employee) employee)) {
              System.out.println("Promotion Successful");
            } else {
              System.out.println("Promotion Unsuccessful");
            }
          } catch (UserNotFoundException | InvalidRoleException e) {
            ExceptionHandler.handle(e);
          }

        } else if (input == 2) {
          readBooks();
        } else if (input == 3) {
          getHistoricAccounts(bufferedReader);
        } else if (input == 4) {
          getActiveAccounts(bufferedReader);
        } else if (input == 5) {
          getAllAccounts(bufferedReader);
        } else if (input == 6) {
          Database.backupDatabase();
        }
      } while (input != 0);
    } catch (Exception exception) {
      ExceptionHandler.handle(exception);
    } finally {
      System.out.println("End of Session.");
    }
  }

  private static void getHistoricAccounts(BufferedReader bufferedReader) {
    int userId;
    List<Integer> accountIds = new ArrayList<Integer>();
    System.out.println("Please enter the id of the user");
    userId = getIntegerInput(bufferedReader);
    try {
      accountIds = DatabaseSelectHelper
          .getUserInactiveAccountIds(DatabaseSelectHelper.getUserDetails(userId));
    } catch (SQLException | InputException | ItemNotFoundException
        | UserNotFoundException error) {
      ExceptionHandler.handle(error);
    }
    if (accountIds == null || accountIds.isEmpty()) {
      System.out.println("Inactive accounts not found for user.");
    } else {
      System.out.println("These are the inactive accounts for this user:");
      for (int ids : accountIds) {
        System.out.println(ids);
      }
    }
  }
  
  private static void getAllAccounts(BufferedReader bufferedReader) {
    int userId;
    List<Integer> accountIds = new ArrayList<Integer>();
    System.out.println("Please enter the id of the user");
    userId = getIntegerInput(bufferedReader);
    try {
      accountIds = DatabaseSelectHelper
          .getUserAccountIds(DatabaseSelectHelper.getUserDetails(userId));
    } catch (SQLException | InputException | ItemNotFoundException
        | UserNotFoundException error) {
      ExceptionHandler.handle(error);
    }
    if (accountIds == null || accountIds.isEmpty()) {
      System.out.println("Accounts not found for user.");
    } else {
      System.out.println("These are all accounts for this user:");
      for (int ids : accountIds) {
        System.out.println(ids);
      }
    }
  }

  private static void getActiveAccounts(BufferedReader bufferedReader) {
    int userId;
    List<Integer> accountIds = new ArrayList<Integer>();
    System.out.println("Please enter the id of the user");
    userId = getIntegerInput(bufferedReader);
    try {
      accountIds =
          DatabaseSelectHelper.getUserActiveAccountIds(DatabaseSelectHelper.getUserDetails(userId));
    } catch (SQLException | InputException | ItemNotFoundException
        | UserNotFoundException error) {
      ExceptionHandler.handle(error);
    }
    if (accountIds == null || accountIds.isEmpty()) {
      System.out.println("Active accounts not found for user.");
    } else {
      System.out.println("These are the active accounts for this user:");
      for (int ids : accountIds) {
        System.out.println(ids);
      }
    }
  }

  private static void printEmployeeList() throws SQLException {
    List<User> employees = DatabaseSelectHelper.getUsersDetails();
    System.out.println("Current Employees:");
    for (User user : employees) {
      if (DatabaseSelectHelper.getRoleName(user.getRoleId()).toString()
          .equals(Roles.EMPLOYEE.toString())) {
        System.out.println("Employee name: " + user.getName() + " ID: " + user.getId());
      }
    }
  }

  /**
   * Create a context menu, where the user is prompted with: 1 - Employee Login 2 - Customer Login 0
   * - Exit Enter Selection: this will bring them to the appropriate menu, or re-prompt if anything
   * else is inputed.
   * 
   * @param bufferedReader reads user input
   * @throws Exception GENERAL EXCEPTION
   */
  private static void menuPrompt(BufferedReader bufferedReader) throws Exception {

    int input = 4;
    while (true) {
      System.out.println("1. Employee Login 2. Customer Login 0. Exit Enter Selection");
      input = getIntegerInput(bufferedReader);
      if (input == 1) {
        employeePrompt(bufferedReader);
      } else if (input == 2) {
        customerPrompt(bufferedReader);
      }
      if (input == 0) {
        break;
      }
    }
  }

  /**
   * Create a context menu for the Employee interface Prompt the employee for their id and password
   * Attempt to authenticate them. If the Id is not that of an employee or password is incorrect,
   * end the session If the Id is an employee, and the password is correct, create an
   * EmployeeInterface object then give them the following options: 1. authenticate new employee 2.
   * Make new User 3. Make new account 4. Make new Employee 5. Restock Inventory 6. Exit.
   * 
   * @param bufferedReader read user input
   */
  private static void employeePrompt(BufferedReader bufferedReader) {
    User user;

    do {
      try {
        user = authenticateUser(bufferedReader, Roles.EMPLOYEE);
      } catch (Exception error) {
        ExceptionHandler.handle(error);
        if (continueCheck(bufferedReader)) {
          user = null;
        } else {
          return;
        }
      }
    } while (user == null);

    int input = -100000;
    do {
      try {
        EmployeeInterface employeeInterface =
            new EmployeeInterface(DatabaseSelectHelper.getInventory());
        employeeInterface.setCurrentEmployee((Employee) user);
        do {
          System.out.println("1. authenticate new employee 2. Make new User 3. Make new account "
              + "4. Make new Employee 5. Restock Inventory 6. Exit");
          input = getIntegerInput(bufferedReader);
          if (input == 1) {
            System.out.println("Authenticate new Employee:");
            User newEmp = authenticateEmployee(bufferedReader);
            if (user != null && newEmp.isAuthenticated()) {
              employeeInterface.setCurrentEmployee((Employee) newEmp);
            }
          } else if (input == 2) {
            System.out.println("Make new User: ");
            makeUser(bufferedReader, employeeInterface);
          } else if (input == 3) {
            System.out.println("Make new account:");
            createNewAccount(bufferedReader);
          } else if (input == 4) {
            System.out.println("Make new Employee:");
            createEmployee(bufferedReader, employeeInterface);
          } else if (input == 5) {
            System.out.println("Restock Inventory:");
            System.out.println("Enter Item ID:");
            int itemId = getIntegerInput(bufferedReader);
            System.out.println("Enter Quantity:");
            int quantity = getIntegerInput(bufferedReader);
            if (employeeInterface.restockInventory(DatabaseSelectHelper.getItem(itemId),
                quantity)) {
              System.out.println("Restocked!");
            } else {
              System.out.println("Operation failed");
            }
          }
        } while (input != 6);
      } catch (Exception error) {
        ExceptionHandler.handle(error);
      }
    } while (input != 6);
    System.out.println("End of Session.");
  }

  private static void createNewAccount(BufferedReader reader) throws IOException {
    do {
      System.out.println("Enter Customer ID");
      int userId = getIntegerInput(reader);
      try {
        int id = DatabaseInsertHelper.insertAccount(userId, true);
        System.out.printf("New account id of %s: %d\n",
            DatabaseSelectHelper.getUserDetails(userId).getName(), id);
        return;
      } catch (InputException | UserNotFoundException | SQLException
          | DatabaseInsertException error) {
        ExceptionHandler.handle(error);
        if (!continueCheck(reader)) {
          return;
        }
      }
    } while (true);
  }

  private static boolean continueCheck(BufferedReader reader) {
    System.out.println("Operation failed: 1. Try again -1. Back");
    int input;
    do {
      input = getIntegerInput(reader);
      if (input == -1) {
        return false;
      } else if (input == 1) {
        return true;
      }
    } while (true);
  }

  /**
   * create a context menu for the customer Shopping cart Prompt the customer for their id and
   * password Attempt to authenticate them If the authentication fails or they are not a customer,
   * repeat If they get authenticated and are a customer, give them this menu: 1. List current items
   * in cart 2. Add a quantity of an item to the cart 3. Check total price of items in the cart 4.
   * Remove a quantity of an item from the cart 5. check out 6. Exit When checking out, be sure to
   * display the customers total, and ask them if they wish to continue shopping for a new order For
   * each of these, loop through and continue prompting for the information needed Continue showing
   * the context menu, until the user gives a 6 as input.
   * 
   * @param bufferedReader read user input
   * @throws NegativeQuantityException thrown when a negative quantity is given when a non-negative
   *         value is expected
   * @throws ItemNotFoundException thrown when a given item is not found in the database
   * @throws NullInputException thrown when input is null when expecting a non-null input
   * @throws InsufficientInventoryException thrown when there is insufficient quantity of an item in
   *         the database
   * @throws AuthenticationException thrown if user is not authenticated
   */
  private static void customerPrompt(BufferedReader bufferedReader) {
    User user;

    do {
      try {
        user = authenticateUser(bufferedReader, Roles.CUSTOMER);
      } catch (Exception error) {
        ExceptionHandler.handle(error);
        if (continueCheck(bufferedReader)) {
          user = null;
        } else {
          return;
        }
      }
    } while (user == null);

    Account account;
    do {
      try {
        account = getAccount(bufferedReader, user);
      } catch (AccountNotFoundException | SQLException | InputException | ItemNotFoundException
          | UserNotFoundException error) {
        ExceptionHandler.handle(error);
        if (continueCheck(bufferedReader)) {
          account = null;
        } else {
          return;
        }
      }

      if (account == null && !continueCheck(bufferedReader)) {
        return;
      }

    } while (account == null);

    int input = -10000;
    do {
      try {
        System.out
            .println("1. List current items in cart 2. Add a quantity of an item to the cart \n"
                + "3. Check total price of items in the cart \n"
                + "4. Remove a quantity of an item from the cart 5. check out "
                + "6. refund_view previous purchase 7. Exit");
        input = parseInput(bufferedReader.readLine(), 4);
        if (input == 1) {
          System.out.println("List current items in cart:");
          //for (Item item : account.getCart().getItems()) {
            //System.out.printf("%s (%d): %s each\n", item.getName(),
                //account.getCart().getAmount(item), item.getPrice().toString());
          //}
          System.out.println(printInventory(account));
        } else if (input == 2) {
          printInventory();
          System.out.println("Add a quantity of an item to the cart:");
          System.out.println("Enter Item ID:");
          int itemId = getIntegerInput(bufferedReader);
          System.out.println("Enter Quantity:");
          int quantity = getIntegerInput(bufferedReader);
          account.getCart().addItem(DatabaseSelectHelper.getItem(itemId), quantity);
        } else if (input == 3) {
          System.out.println("Check total price of items in the cart:");
          System.out.println(account.getCart().getTotal());
        } else if (input == 4) {
          System.out.println("Remove a quantity of an item from the cart:");
          System.out.println("Enter Item ID:");
          int itemId = getIntegerInput(bufferedReader);
          System.out.println("Enter Quantity:");
          int quantity = getIntegerInput(bufferedReader);
          account.getCart().removeItem(DatabaseSelectHelper.getItem(itemId), quantity);
        } else if (input == 7) {
          int save;
          while (true) {
            System.out.println("Would you like to save your cart?");
            System.out.println("0 for No 1 for Yes");
            save = getIntegerInput(bufferedReader);
            if (save == 0) {
              break;
            } else if (save == 1) {
              for (Item item : account.getCart().getMap().keySet()) {
                DatabaseInsertHelper.insertAccountLine(account.getId(), item.getId(),
                    account.getCart().getMap().get(item));
              }
              System.out.println();
            }
            break;
          }
          break;
        } else if (input == 5) {
          System.out.println("check out:");
          try {
            if (!account.getCart().checkOut(account.getId())) {
              System.out.println("Insufficient items in Store Inventory");
            }
            System.out.println("Checkout Successful, Thank you for your patronage!");
            break;
          } catch (Exception e) {
            ExceptionHandler.handle(e);
          }
        } else if (input == 6) {
          refundItem(user, bufferedReader);
        }
      } catch (Exception error) {
        ExceptionHandler.handle(error);
      }
    } while (input != 7);
    System.out.println("End of Session.");
  }

  private static String printInventory(Account account) throws ItemNotFoundException{
    String cart = "";
    for (Item item : account.getCart().getItems()) {
      cart = cart + "%s (%d): %s each\n" + item.getName() + account.getCart().getAmount(item)
              + item.getPrice().toString();
    }
    return cart;
  }

  private static void refundItem(User user, BufferedReader bufferedReader)
      throws ItemNotFoundException, SQLException {
    int refundItemId = -1;
    Integer numberOfItemsRefunded = 0;
    int totalItemsBought = 0;
    List<Sale> pastSalesFromUser = null;
    try {
      pastSalesFromUser = DatabaseSelectHelper.getSalesToUser(user.getId());
    } catch (SQLException | ItemNotFoundException | UserNotFoundException
        | SaleNotFoundException error) {
      ExceptionHandler.handle(error);
    }
    do {
      try {
        System.out.println("Please enter the id of the item you want to return.");
        printInventory();
        refundItemId = getIntegerInput(bufferedReader);
        System.out.println("Please enter the number of items you want refunded.");
        numberOfItemsRefunded = getIntegerInput(bufferedReader);
        for (int i = 0; i < pastSalesFromUser.size(); i++) {
          totalItemsBought = totalItemsBought
              + DatabaseSelectHelper.getItemizedSaleHashMap(pastSalesFromUser.get(i).getId())
                  .get(DatabaseSelectHelper.getItem(refundItemId));
        }
      } catch (SQLException | ItemNotFoundException | UserNotFoundException
          | SaleNotFoundException error) {
        ExceptionHandler.handle(error);
      }
    } while (numberOfItemsRefunded < 1 | numberOfItemsRefunded > totalItemsBought);
    try {
      DatabaseUpdateHelper.updateInventoryQuantity(
          DatabaseSelectHelper.getInventory().getItemMap()
              .get(DatabaseSelectHelper.getItem(refundItemId)) + numberOfItemsRefunded,
          refundItemId);
    } catch (SQLException | InputException | ItemNotFoundException error) {
      ExceptionHandler.handle(error);
    }
    BigDecimal num = new BigDecimal(numberOfItemsRefunded.toString());
    try {
      int saleId = DatabaseInsertHelper.insertRefund(user.getId(),
          DatabaseSelectHelper.getItem(refundItemId).getPrice().multiply(num).multiply(TAXRATE)
              .negate().setScale(2, BigDecimal.ROUND_HALF_UP));
      DatabaseInsertHelper.insertItemizedRefund(saleId, refundItemId, numberOfItemsRefunded * (-1));
    } catch (UserNotFoundException | DatabaseInsertException | SQLException | ItemNotFoundException
        | SaleNotFoundException | InputException error) {
      ExceptionHandler.handle(error);
    }
    System.out.println("RefundView Seccessful, the " + numberOfItemsRefunded + " "
        + DatabaseSelectHelper.getItem(refundItemId).toString()
        + " has been returned. along with your $" + DatabaseSelectHelper.getItem(refundItemId)
            .getPrice().multiply(num).multiply(TAXRATE).setScale(2, BigDecimal.ROUND_HALF_UP));
  }

  private static Account getAccount(BufferedReader bufferedReader, User user)
      throws AccountNotFoundException, SQLException, InputException, ItemNotFoundException,
      UserNotFoundException {
    List<Account> accounts = DatabaseSelectHelper.getUserActiveAccounts(user);
    if (accounts == null || accounts.isEmpty()) {
      System.out.println("Accounts not found for user, Please create an account.");
      return null;
    }

    System.out.println("Enter account Number");
    System.out.println("Available Accounts");
    for (Account account : accounts) {
      System.out.println(account.getId());
    }

    int accountId = getIntegerInput(bufferedReader);
    for (Account account : accounts) {
      if (account.getId() == accountId) {
        return account;
      }
    }

    try {
      throw new AccountNotFoundException();
    } catch (AccountNotFoundException error) {
      ExceptionHandler.handle(error);
      if (continueCheck(bufferedReader)) {
        return getAccount(bufferedReader, user);
      } else {
        throw new AccountNotFoundException();
      }
    }
  }

  private static void printInventory() {
    try {
      Inventory inventory = DatabaseSelectHelper.getInventory();
      System.out.println("Total Items: " + " " + inventory.getTotalItems());
      for (Item item : inventory.getItemMap().keySet()) {
        System.out.println(item.getId() + " " + item.getName() + " " + item.getPrice()
            + " Amount Left " + inventory.getItemMap().get(item));
      }
    } catch (Exception error) {
      ExceptionHandler.handle(error);
    }
  }

  private static void makeUser(BufferedReader bufferedReader, EmployeeInterface employeeInterface) {
    int userId;
    int age;
    String name;
    String address;
    String password;
    while (true) {
      try {
        System.out.println("Enter user's name");
        name = bufferedReader.readLine();
        System.out.println("Enter users's age (integer value)");
        age = getIntegerInput(bufferedReader);
        System.out.println("Enter users's address");
        address = bufferedReader.readLine();
        System.out.println("Enter users's password:");
        password = bufferedReader.readLine();
        userId = employeeInterface.createCustomer(name, age, address, password);
        System.out.println("New User ID:" + userId + " Name: " + name);
        return;
      } catch (InputException | SQLException | DatabaseInsertException | IOException e) {
        ExceptionHandler.handle(e);
        if (!continueCheck(bufferedReader)) {
          return;
        }
      }
    }
  }

  private static void createEmployee(BufferedReader bufferedReader,
      EmployeeInterface employeeInterface) {
    int userId;
    int age;
    String name;
    String address;
    String password;
    while (true) {
      try {
        System.out.println("Enter user's name");
        name = bufferedReader.readLine();
        System.out.println("Enter users's age (integer value)");
        age = getIntegerInput(bufferedReader);
        System.out.println("Enter users's address");
        address = bufferedReader.readLine();
        System.out.println("Enter users's password:");
        password = bufferedReader.readLine();
        userId = employeeInterface.createEmployee(name, age, address, password);
        System.out.println("New Employee ID:" + userId + " Name: " + name);
        return;
      } catch (InputException | SQLException | DatabaseInsertException | IOException e) {
        ExceptionHandler.handle(e);
        if (!continueCheck(bufferedReader)) {
          return;
        }
      }
    }
  }


  private static Employee authenticateEmployee(BufferedReader bufferedReader)
      throws IOException, SQLException, UserNotFoundException {
    User employee;
    System.out.println("Enter the ID of the employee to be Authenticated");
    int userId = getIntegerInput(bufferedReader);
    System.out.println("Enter the new employee's password:");
    String password = bufferedReader.readLine();
    employee = DatabaseSelectHelper.getUserDetails(userId);
    if (DatabaseSelectHelper.getRoleName(employee.getRoleId()).toString()
        .equals(Roles.EMPLOYEE.toString())) {
      System.out.println("Employee successfully authenticated.");
      return (Employee) employee;
    } else {
      System.out.println("Error with user authentication");
      return null;
    }
  }


  /**
   * Prompts and returns for an authenticated user, and returns true if ID matches and password and
   * the user that matches the ID is of the given type.
   * 
   * @param bufferedReader reads user input
   * @param type type of expected user
   * @return null if unsuccessful authentication, authenticated user of given type if successful
   * @throws SQLException thrown for errors accessing database
   * @throws UserNotFoundException thrown when wanted user is not found within the database
   * @throws InvalidRoleException thrown when a user is a different role than wanted
   * @throws InvalidCredentialsException thrown when given user credentials are invalid
   */
  private static User authenticateUser(BufferedReader bufferedReader, Roles type)
      throws SQLException, UserNotFoundException, InvalidRoleException,
      InvalidCredentialsException {
    System.out.printf("Enter your %s ID:\n", type.name());
    int userId = getIntegerInput(bufferedReader);
    String password;
    while (true) {
      System.out.println("Enter your Password:");
      try {
        password = bufferedReader.readLine();
        break;
      } catch (IOException e) {
        System.out.println("Invalid Password Input, Try Again.");
      }
    }
    User user;
    user = DatabaseSelectHelper.getUserDetails(userId);
    if (!DatabaseSelectHelper.getRoleName(user.getRoleId()).toString().equals(type.toString())) {
      throw new InvalidRoleException();
    } else  {
      throw new InvalidCredentialsException();
    }

  }

  /**
   * Extracts the int from a given string, returns the default value on bad input.
   * 
   * @param input String to extract an integer
   * @param defaultValue value to return if input can not become int
   * @return integer from input if available, or default value on error.
   */
  private static int parseInput(String input, int defaultValue) {
    int parsed;
    try {
      parsed = Integer.parseInt(input);
    } catch (NumberFormatException e) {
      parsed = defaultValue;
    }
    return parsed;
  }

  private static int getIntegerInput(BufferedReader bufferedReader) {
    int input;
    try {
      input = Integer.parseInt(bufferedReader.readLine());
    } catch (NumberFormatException e) {
      input = getIntegerInput(bufferedReader);
    } catch (IOException e) {
      input = getIntegerInput(bufferedReader);
    }
    return input;
  }

  private static void readBooks()
      throws SQLException, ItemNotFoundException, UserNotFoundException, SaleNotFoundException {
    SalesLog log = DatabaseSelectHelper.getItemizedSales();
    HashMap<Integer, Sale> saleMap = log.getFullSale();
    HashMap<Item, Integer> itemMap = log.getItemMap();

    for (Integer saleId : saleMap.keySet()) {
      System.out.println("Customer : " + saleMap.get(saleId).getUser().getName());
      System.out.println("Purchase Number : " + saleId);
      System.out.print("Itemized Breakdown: ");
      for (Item item : saleMap.get(saleId).getItemMap().keySet()) {
        System.out.println(item.getName() + ": " + saleMap.get(saleId).getItemMap().get(item));

      }
      int nthItem = 1;
      while (itemMap.containsKey(DatabaseSelectHelper.getItem(nthItem))) {
        System.out.println(
            "Number " + nthItem + " Sold: " + itemMap.get(DatabaseSelectHelper.getItem(nthItem)));
      }
    }
    // TODO make it so you dont have to update :(((
    ((SalesLogImpl) log).updateTotalSold();
    System.out.println("TOTAL SALES: " + log.getTotalSold().toString());
  }
}
