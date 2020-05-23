package com.b07.users;

public class Employee extends User {
  /**
   * id for serialization.
   */
  private static final long serialVersionUID = -408982874696648639L;

  /**
   * Constructor.
   * 
   * @param id if of user
   * @param name of user
   * @param age age of user
   * @param address address of user
   */

  public Employee(int id, String name, int age, String address) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.setAddress(address);
  }

  /**
   * Constructor.
   * 
   * @param id if of user
   * @param name of user
   * @param age age of user
   * @param address address of user
   * @param authenticated boolean for user is authenticated
   */

  public Employee(int id, String name, int age, String address, boolean authenticated) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.setAddress(address);
  }
}
