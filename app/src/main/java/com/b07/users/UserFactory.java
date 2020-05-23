package com.b07.users;

import android.content.Context;

import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.database.helper.DatabaseSelectHelper;
import java.sql.SQLException;

public class UserFactory {
  /**
   * Creates a user from the given data. The child type of user generated is based on the roleId.
   * Assumes all data given is valid.
   * 
   * @param roleId id of role wanted for user
   * @param userId id of the user in the database
   * @param name name of the user
   * @param age age of the user
   * @param address address of the user
   * @return
   */

  public static User buildUser(int roleId, int userId, String name, int age,
      String address, Context context) {
    DatabaseAndroidHelper db = new DatabaseAndroidHelper(context);
    String roleName;
      roleName = db.getRoleHelper(roleId);

    if (roleName.equals(Roles.ADMIN.toString())) {
      return new Admin(userId, name, age, address);
    } else if (roleName.equals(Roles.EMPLOYEE.toString())) {
      return new Employee(userId, name, age, address);
    } else if (roleName.equals(Roles.CUSTOMER.toString())) {
      return new Customer(userId, name, age, address);
    } else {
      return null;
    }
  }
}
