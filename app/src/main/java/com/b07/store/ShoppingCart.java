package com.b07.store;

import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.database.helper.DatabaseUpdateHelper;
import com.b07.exceptions.AccountNotFoundException;
import com.b07.exceptions.AuthenticationException;
import com.b07.exceptions.DatabaseInsertException;
import com.b07.exceptions.IncorrectActivityException;
import com.b07.exceptions.InputException;
import com.b07.exceptions.InsufficientInventoryException;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.NegativeQuantityException;
import com.b07.exceptions.NullInputException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Item;
import com.b07.users.Customer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class ShoppingCart implements Serializable {

  /**
   * id for serialization.
   */
  private static final long serialVersionUID = -5955187026522473481L;
  private HashMap<Item, Integer> items;
  private Customer customer;
  private BigDecimal total;
  private static final BigDecimal TAXRATE = new BigDecimal("1.13");

  /**
   * Constructor.
   * 
   * @param customer user
   * @throws NullInputException if input is null
   * @throws AuthenticationException if user not authenticated.
   */
  public ShoppingCart(Customer customer) throws InputException {
    if (customer == null) {
      throw new NullInputException();
    } else if (!customer.isAuthenticated()) {
      throw new AuthenticationException();
    }
    this.customer = customer;
    total = new BigDecimal("0.00");
    items = new HashMap<Item, Integer>();
  }

  /**
   * Add item to cart.
   * 
   * @param item item to be added
   * @param quantity quantity of item
   * @throws NullInputException if null
   * @throws NegativeQuantityException if negative quantity
   */
  public void addItem(Item item, int quantity) throws InputException {

    if (item == null) {
      throw new NullInputException();
    } else if (quantity < 0) {
      throw new NegativeQuantityException();
    }
    if (!items.containsKey(item)) {
      items.put(item, quantity);
      System.out.println(item);
      total =
          total.add(new BigDecimal(item.getPrice().toString()).multiply(new BigDecimal(quantity)));
      System.out
          .println(new BigDecimal(item.getPrice().toString()).multiply(new BigDecimal(quantity)));
    } else {
      items.put(item, items.get(item) + quantity);
      total = total.add(new BigDecimal(item.getPrice().toString())
          .multiply(new BigDecimal(new Integer(quantity).toString())));
    }
  }

  /**
   * Remove item from cart.
   * 
   * @param item item to be removed
   * @param quantity quantity to be removed
   * @throws NegativeQuantityException if quantity is negative
   * @throws ItemNotFoundException item not in cart
   * @throws InsufficientInventoryException insufficient quantity in cart to remove
   * @throws NullInputException input is null
   */
  public void removeItem(Item item, int quantity)
      throws InputException, ItemNotFoundException, InsufficientInventoryException {
    if (item == null) {
      throw new NullInputException();
    } else if (quantity < 0) {
      throw new NegativeQuantityException();
    } else if (!items.containsKey(item)) {
      throw new ItemNotFoundException();
    } else if (items.get(item) < quantity) {
      throw new InsufficientInventoryException();
    }

    if (items.get(item) == quantity) {
      items.remove(item);
      total = total.subtract(total);
      System.out.printf("%s: 0 left in cart after $d removed.", item, quantity);
    } else {
      items.put(item, items.get(item) - quantity);
      BigDecimal subtractPrice =
          (item.getPrice().multiply(new BigDecimal(String.format("%d.00", quantity)))).setScale(2);
      total = total.subtract(subtractPrice);
      System.out.printf("%s: $d left in cart after $d removed.", item, items.get(item), quantity);
    }
  }

  /**
   * Get list.
   * 
   * @return itemList list of items
   */
  public List<Item> getItems() {

    List<Item> itemList = new ArrayList<>();
    for (Item item : items.keySet()) {
      itemList.add(item);
    }
    return itemList;
  }

  public Customer getCustomer() {
    return customer;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public BigDecimal getTaxRate() {
    return TAXRATE;
  }

  public void clearCart() {
    total = new BigDecimal("0.00");
    items.clear();
  }

  /**
   * Check out items.
   * 
   * @return if operation was successful
   * @throws SQLException db error
   * @throws InsufficientInventoryException inventory does not have enough items.
   * @throws ItemNotFoundException Item not in DB
   * @throws UserNotFoundException User not in DB
   * @throws InputException thrown for bad inputs
   * @throws DatabaseInsertException thrown for error while inserting data into the database
   * @throws SaleNotFoundException thrown when a wanted sale is not found in the database
   * @throws AccountNotFoundException thrown for when a wanted account is not found in the database
   * @throws IncorrectActivityException exception for accessing without authorization
   */

  public boolean checkOut(int accountId) throws SQLException, InsufficientInventoryException,
      ItemNotFoundException, UserNotFoundException, InputException, SaleNotFoundException,
      DatabaseInsertException, AccountNotFoundException, IncorrectActivityException {
    System.out.println(customer.getId());
    if (DatabaseSelectHelper.getUserDetails(customer.getId()) == null) {
      return false;
    }

    for (Item item : items.keySet()) {
      if (DatabaseSelectHelper.getInventory().getItemMap().get(item) < items.get(item)) {
        throw new InsufficientInventoryException();
      }
    }

    for (Item item : items.keySet()) {
      DatabaseUpdateHelper.updateInventoryQuantity(
          DatabaseSelectHelper.getInventory().getItemMap().get(item) - items.get(item),
          item.getId());
    }

    int saleId = DatabaseInsertHelper.insertSale(customer.getId(), total);
    for (Item item : items.keySet()) {
      DatabaseInsertHelper.insertItemizedSale(saleId, item.getId(), items.get(item));
    }

    clearCart();
    DatabaseUpdateHelper.updateAccountStatus(accountId, false);
    return true;
  }

  /**
   * returns the amount of a given item in the shopping cart.
   * 
   * @param item wanted item
   * @return number of item in shopping cart
   * @throws ItemNotFoundException thrown if the item is not found in the cart
   */
  public int getAmount(Item item) throws ItemNotFoundException {
    if (items.containsKey(item)) {
      return items.get(item);
    } else {
      throw new ItemNotFoundException();
    }
  }

  public HashMap<Item, Integer> getMap() {
    return items;
  }

}
