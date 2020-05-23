package com.b07.inventory;

import java.io.Serializable;
import java.util.HashMap;

public class InventoryImpl implements Inventory, Serializable {

  /**
   * id for serialization.
   */
  private static final long serialVersionUID = 2608877040554098818L;
  HashMap<Item, Integer> itemMap;
  private int totalItems;

  /**
   * Set default values for Object.
   */
  public InventoryImpl() {
    itemMap = new HashMap<Item, Integer>();
    totalItems = 0;
  }

  /**
   * Getter for itemMap.
   */
  @Override
  public HashMap<Item, Integer> getItemMap() {
    return itemMap;
  }

  /**
   * Checks itemMaps and then sets.
   */
  @Override
  public void setItemMap(HashMap<Item, Integer> itemMap) {
    this.itemMap = itemMap;
  }

  /**
   * Checks if parameters are valid and then puts them into field itemMap.
   * 
   * @param item Item to be inputed.
   * @param value Value to be inputed.
   */
  @Override
  public void updateMap(Item item, int value) {
    itemMap.put(item, value);
  }

  /**
   * Setter for totalItems.
   * 
   * @return totalItems Total item in inventory.
   */
  @Override
  public int getTotalItems() {
    return totalItems;
  }

  /**
   * Checks and sets totalItems.
   */
  @Override
  public void setTotalItems(int total) {
    totalItems = total;
  }
}
