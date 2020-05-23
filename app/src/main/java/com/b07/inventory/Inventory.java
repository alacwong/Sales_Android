package com.b07.inventory;

import java.io.Serializable;
import java.util.HashMap;

public interface Inventory extends Serializable {

  public HashMap<Item, Integer> getItemMap();

  public void setItemMap(HashMap<Item, Integer> itemMap);

  public void updateMap(Item item, int value);

  public int getTotalItems();

  public void setTotalItems(int total);

}
