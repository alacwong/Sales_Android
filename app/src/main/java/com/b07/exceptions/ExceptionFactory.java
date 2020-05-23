package com.b07.exceptions;

public class ExceptionFactory {
  /**
   * Returns a new Exception based on String e.
   * 
   * @param e the name of the exception to be thrown.
   * @return the specific Exception if found, else Exception.
   */
  public static InputException createException(String e) {
    if (e.equals("AuthenticationException")) {
      return new AuthenticationException();
    } else if (e.equals("InvalidCombinationException")) {
      return new InvalidCombinationException();
    } else if (e.equals("InvalidCredentialsException")) {
      return new InvalidCredentialsException();
    } else if (e.equals("InvalidNameException")) {
      return new InvalidNameException();
    } else if (e.equals("NegativeQuantityException")) {
      System.out.println("Negative quantity!");
      return new NegativeQuantityException();
    } else if (e.equals("NullInputExceptionException")) {
      return new NullInputException();
    } else if (e.equals("PrecisionException")) {
      return new PrecisionException();
    } else if (e.equals("StringLengthException")) {
      return new StringLengthException();
    } else if (e.equals("TotalPriceMismatchException")) {
      return new TotalPriceMismatchException();
    } else if (e.equals("InvalidRoleException")) {
      return new InvalidRoleException();
    } else {
      return new InputException();
    }
  }
}
