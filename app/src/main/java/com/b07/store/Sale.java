package com.b07.store;

import com.b07.inventory.Item;
import com.b07.users.User;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

public interface Sale extends Serializable {

  public int getId();

  public void setId(int id);

  public User getUser();

  public void setUser(User user);

  public BigDecimal getTotalPrice();

  public void setTotalPrice(BigDecimal price);

  public HashMap<Item, Integer> getItemMap();

  public void setItemMap(HashMap<Item, Integer> itemMap);

  public void addTotalPrice(BigDecimal multiply);
}

