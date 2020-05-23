package com.b07.inventory;

import com.b07.exceptions.NegativeQuantityException;
import com.b07.exceptions.NullInputException;
import java.util.HashMap;
/**
 * Build class for InventoryImpl.
 * @author Alac Wong
 * 
 *
 */

public class InventoryBuilder {
  
  InventoryImpl inventory;
  
  /**
   * Build default inventory.
   */
  public InventoryBuilder() {
    inventory = new InventoryImpl();
  }
  
  /**
   * Builds with itemMap after checking it.
   * @param itemMap Variable to be set.
   * @return Returns this Object with itemMap set.
   * @throws NullInputException Throws this exception if itemMap is null.
   */
  public InventoryBuilder itemMap(HashMap<Item, Integer> itemMap) {
    inventory.setItemMap(itemMap);
    return this;
  }
  
  /**
   * Builds with totalItems after checking it.
   * @param totalItems Variable to be set
   * @return Returns this Object.
   * @throws NegativeQuantityException Throws this if user inputs null input.
   */
  public InventoryBuilder totalItems(int totalItems) {
    inventory.setTotalItems(totalItems);
    return this;
  }
  
  /**
   * Builds inventoryImpl.
   * @return built inventory object
   */
  public InventoryImpl build() {
    return inventory;
  }

}
