package com.b07.users;

import com.b07.exceptions.InputException;
import com.b07.inventory.Item;
import com.b07.store.ShoppingCart;
import java.io.Serializable;

public class Account implements Serializable {

  /**
   * id for serialization.
   */
  private static final long serialVersionUID = -2249629472029568789L;
  private ShoppingCart cart;
  private int id;
  Customer customer;
  private boolean active;

  public Account(int id, ShoppingCart cart) {
    this.id = id;
    this.cart = cart;
  }

  public void setCart(ShoppingCart cart) {
    this.cart = cart;
  }

  public void updateCart(Item item, int quantity) throws InputException {
    cart.addItem(item, quantity);
  }

  public ShoppingCart getCart() {
    return cart;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public boolean getActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
