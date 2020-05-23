package com.b07.store;

import com.b07.inventory.Item;
import com.b07.users.User;
import java.math.BigDecimal;
import java.util.HashMap;

public class SaleImpl implements Sale {

  /**
   * id for serialization.
   */
  private static final long serialVersionUID = -2067031801074384970L;
  private int id;
  private User user;
  private BigDecimal totalPrice;
  HashMap<Item, Integer> itemMap;


  /**
   * Constructor for SaleImpl.
   * 
   * @param id Id of sale
   * @param user User of sale
   * @param totalPrice Total price of the sale
   * @param itemMap Dictionary for items and prices
   */
  public SaleImpl(int id, User user, BigDecimal totalPrice, HashMap<Item, Integer> itemMap) {
    setId(id);
    setUser(user);
    setItemMap(itemMap);
    setTotalPrice(totalPrice);
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public User getUser() {
    return user;
  }

  @Override
  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  @Override
  public void setTotalPrice(BigDecimal price) {
    totalPrice = price;
  }


  @Override
  public HashMap<Item, Integer> getItemMap() {
    return itemMap;
  }

  @Override
  public void setItemMap(HashMap<Item, Integer> itemMap) {
    this.itemMap = itemMap;
  }

  @Override
  public void addTotalPrice(BigDecimal price) {
    totalPrice = totalPrice.add(new BigDecimal(price.toString()));
  }

}
