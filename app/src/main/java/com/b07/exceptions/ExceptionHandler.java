package com.b07.exceptions;

public class ExceptionHandler {

  static private final boolean DEBUG = false;
  static private final String pack = "com.b07.exceptions.";

  /**
   * Checks className and prints message associated with that exception.
   * 
   * @param error Exception to handle
   */
  public static String handle(Exception error) {
    System.out.println("debugging");
    if (DEBUG) {
      System.err.println("EXCEPTION STACK TRACE (DW) IT IS HANDLED");
      error.printStackTrace();
    }
    if (error.getClass().getName().equals(pack + "AuthenticationException")) {
      return ("Authentication exception!");
    } else if (error.getClass().getName().equals(pack + "AccountNotFoundException")) {
      return ("Account not found");
    } else if (error.getClass().getName().equals(pack + "AccountNotFoundException")) {
      return ("Account not detected");
    } else if (error.getClass().getName().equals(pack + "ConnectionFailedException")) {
     return ("Connection failed!");
    } else if (error.getClass().getName().equals(pack + "DatabaseInsertException")) {
      return ("Exception when inserting into database!");
    } else if (error.getClass().getName().equals(pack + "InsufficientInventoryException")) {
      return ("Insufficient inventory!");
    } else if (error.getClass().getName().equals("InvalidCombinationException")) {
      return ("Invalid combination. Try again.");
    } else if (error.getClass().getName().equals(pack + "InvalidCredentialsException")) {
      return ("Login failed. Try again.");
    } else if (error.getClass().getName().equals(pack + "InvalidNameException")) {
      return ("Name is invalid!");
    } else if (error.getClass().getName().equals(pack + "InvalidRoleException")) {
      return ("Role is invalid!");
    } else if (error.getClass().getName().equals(pack + "ItemNotFoundException")) {
      return ("Item not found!");
    } else if (error.getClass().getName().equals(pack + "NegativeQuantityException")) {
      return ("Negative quantity!");
    } else if (error.getClass().getName().equals(pack + "NullInputExceptionException")) {
      return ("Input is null!");
    } else if (error.getClass().getName().equals(pack + "PrecisionException")) {
     return ("Invalid currency expression!");
    } else if (error.getClass().getName().equals(pack + "RoleIdNotFoundException")) {
      return ("RoleID not found!");
    } else if (error.getClass().getName().equals(pack + "SaleNotFoundException")) {
      return ("Sale not found!");
    } else if (error.getClass().getName().equals(pack + "StringLengthException")) {
     return ("Input is too long! Input should be less than 100 characters.");
    } else if (error.getClass().getName().equals(pack + "TotalPriceMismatchException")) {
      return ("Price doesn't match the total price!");
    } else if (error.getClass().getName().equals(pack + "UserNotFoundException")) {
     return ("User not found!");
    } else if (error.getClass().getName().equals(pack + "IncorrectActivityException")) {
     return ("Account cannot be updated to active!");
    } else if (error.getClass().getName().equals(pack + "RefundException")) {
      return ("Number of items to be refunded inappropriate!");
    } else {
      if (DEBUG) {
        System.out.println("Make sure to add " + error.getClass().getName() + " to the handler");
      }
      return ("Error!");
    }
  }
}
