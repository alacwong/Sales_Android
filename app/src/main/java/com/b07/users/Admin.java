package com.b07.users;

import com.b07.database.helper.DatabaseInsertHelper;
import com.b07.database.helper.DatabaseUpdateHelper;
import java.sql.SQLException;


public class Admin extends User {

  /**
   * id for serialization.
   */
  private static final long serialVersionUID = -1151547704514718341L;

  /**
   * Constuctor.
   * 
   * @param id if of admin.
   * @param name name of admin
   * @param age age of admin
   * @param address address of admin
   */
  public Admin(int id, String name, int age, String address) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.setAddress(address);
  }

  /**
   * Constuctor with authenticated user.
   * 
   * @param id if of admin.
   * @param name name of admin
   * @param age age of admin
   * @param address address of admin
   * @param authenticated user is authenticated
   */
  public Admin(int id, String name, int age, String address, boolean authenticated) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.setAddress(address);
  }

  /**
   * Change role of user.
   * 
   * @param employee employee to be promoted
   * @return if operation was successful
   */

  public boolean promoteEmployee(Employee employee) {
    if (employee == null) {
      return false;
    }
    try {
      int role = DatabaseInsertHelper.insertRole(Roles.ADMIN.toString());
      DatabaseUpdateHelper.updateUserRole(role, employee.getId());
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

}
