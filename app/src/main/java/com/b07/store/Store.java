package com.b07.store;

import com.b07.inventory.Inventory;
import com.b07.inventory.Item;
import com.b07.users.Account;
import com.b07.users.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Store implements Serializable {
  private ArrayList<User> users; // all users
  private Inventory inventory; // inventory
  private ArrayList<Item> items; // all items
  private HashMap<Integer, String> roles; // Maps userids to roles (1) -> ADMIN
  private SalesLog log; // Saleslog for itemized Sales
  private HashMap<Integer, Integer> roleIds; // Maps userIds to roleIds
  private HashMap<Integer, String> password; // Maps userId to password
  private HashMap<Integer, Sale> fullSale; // Maps saleId to Sale

  private HashMap<Integer, Boolean> accountsActive;
  private HashMap<Integer, List<Account>> accounts;
  
  public HashMap<Integer, Boolean> getAccountsActive() {
    return accountsActive;
  }

  public void setAccountsActive(HashMap<Integer, Boolean> accountsActive) {
    this.accountsActive = accountsActive;
  }

  public HashMap<Integer, List<Account>> getAccounts() {
    return accounts;
  }

  public void setAccounts(HashMap<Integer, List<Account>> accounts) {
    this.accounts = accounts;
  }

  /**
   * id for serialization.
   */
  private static final long serialVersionUID = 1409782918403598399L;

  public void setUsers(ArrayList<User> users) {
    this.users = users;
  }

  public ArrayList<User> getUsers() {
    return users;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  public ArrayList<Item> getItems() {
    return items;
  }

  public void setItems(ArrayList<Item> items) {
    this.items = items;
  }

  public HashMap<Integer, String> getRoles() {
    return roles;
  }

  public void setRoles(HashMap<Integer, String> roles) {
    this.roles = roles;
  }

  public SalesLog getLog() {
    return log;
  }

  public void setLog(SalesLog log) {
    this.log = log;
  }

  public HashMap<Integer, Integer> getRoleIds() {
    return roleIds;
  }

  public void setRoleIds(HashMap<Integer, Integer> roleIds) {
    this.roleIds = roleIds;
  }

  public HashMap<Integer, String> getPassword() {
    return password;
  }

  public void setPassword(HashMap<Integer, String> password) {
    this.password = password;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public HashMap<Integer, Sale> getFullSale() {
    return fullSale;
  }

  public void setSalesLog(HashMap<Integer, Sale> fullSale) {
    this.fullSale = fullSale;
  }
}
