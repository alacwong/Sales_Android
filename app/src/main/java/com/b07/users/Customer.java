package com.b07.users;

public class Customer extends User {
  /**
   * id for serialization.
   */
  private static final long serialVersionUID = -7845430689919000752L;

  /**
   * Constructor.
   * 
   * @param id if of user
   * @param name of user
   * @param age age of user
   * @param address address of user
   */

  public Customer(int id, String name, int age, String address) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.setAddress(address);
  }

  /**
   * Constructor.
   * 
   * @param id id of customer
   * @param name name of customer.
   * @param age age of customer.
   * @param address address of user.
   * @param authenticated if user is authenticated.
   */

  public Customer(int id, String name, int age, String address, boolean authenticated) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.setAddress(address);
    super.authenticated = authenticated;
  }


}
