package com.b07.store;

import com.b07.database.helper.DatabaseSelectHelper;
import com.b07.exceptions.ItemNotFoundException;
import com.b07.exceptions.SaleNotFoundException;
import com.b07.exceptions.UserNotFoundException;
import com.b07.inventory.Item;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SalesLogImpl implements SalesLog {

  /**
   * id for serialization.
   */
  private static final long serialVersionUID = -7106619792630351715L;
  HashMap<Integer, List<Sale>> logMap;
  HashMap<Item, Integer> itemMap;
  HashMap<Integer, Sale> saleMap;
  BigDecimal totalSold;

  /**
   * Constructor for implementation of SalesLog interface.
   */
  public SalesLogImpl() {
    logMap = new HashMap<Integer, List<Sale>>();
    itemMap = new HashMap<Item, Integer>();
    totalSold = new BigDecimal("0.00");
    saleMap = new HashMap<Integer, Sale>();
  }

  @Override
  public HashMap<Integer, List<Sale>> getLogMap() {
    return logMap;
  }

  public HashMap<Item, Integer> getItemMap() {
    return itemMap;
  }

  /**
   * returns the full history of sales in the database.
   */
  public HashMap<Integer, Sale> getFullSale()
      throws SQLException, UserNotFoundException, SaleNotFoundException, ItemNotFoundException {
    for (Integer saleId : logMap.keySet()) {
      saleMap.put(saleId, DatabaseSelectHelper.getSaleById(saleId));
    }
    return saleMap;
  }

  public BigDecimal getTotalSold() {
    return totalSold;
  }

  /**
   * updates the total sales price for the sales history. TODO this is a bandaid fix, integrate into
   * updateMap.
   */
  public void updateTotalSold() {
    for (int saleId : saleMap.keySet()) {
      HashMap<Item, Integer> saleItemMap = saleMap.get(saleId).getItemMap();
      for (Item item : saleItemMap.keySet()) {
        BigDecimal itemPrice = item.getPrice();
        BigDecimal quantity = new BigDecimal(saleItemMap.get(item).toString());
        totalSold = totalSold.add(itemPrice.multiply(quantity));
      }
    }
  }

  @Override
  public void updateMap(int saleId, Sale sale) {
    if (logMap.containsKey(saleId)) {
      logMap.get(saleId).add(sale);
    } else {
      ArrayList<Sale> temp;
      temp = new ArrayList<Sale>();
      temp.add(sale);
      logMap.put(saleId, temp);
      for (Item item : sale.getItemMap().keySet()) {
        if (itemMap.containsKey(item)) {
          itemMap.put(item, itemMap.get(item) + sale.getItemMap().get(item));
          totalSold = totalSold.add(
              item.getPrice().multiply(new BigDecimal(sale.getItemMap().get(item).toString())));
        }
      }
    }
    updateTotalSold();
  }
}
