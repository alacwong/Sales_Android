package com.b07.store;

import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Item;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface SalesLog extends Serializable {

  public HashMap<Integer, List<Sale>> getLogMap();

  public void updateMap(int saleId, Sale sale);

  public HashMap<Integer, Sale> getFullSale()
      throws SQLException, UserNotFoundException, SaleNotFoundException, ItemNotFoundException;

  public HashMap<Item, Integer> getItemMap();

  public BigDecimal getTotalSold();

}
