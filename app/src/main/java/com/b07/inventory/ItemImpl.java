package com.b07.inventory;

import com.b07.exceptions.InvalidNameException;
import com.b07.exceptions.NegativeQuantityException;
import com.b07.exceptions.NullInputException;
import com.b07.exceptions.PrecisionException;
import com.b07.exceptions.StringLengthException;
import java.math.BigDecimal;

/**
 * Item data object.
 * 
 * @author Alac Wong
 *
 */

public class ItemImpl implements Item {

  /**
   * id for serialization.
   */
  private static final long serialVersionUID = -347519021876024048L;
  private int id;
  private String name;
  private BigDecimal price;

  /**
   * Constructor of itemImpl.
   * 
   * @param id Id of item
   * @param name Name of item
   * @param price Price of item
   * @throws StringLengthException Throws if name is too long
   * @throws NullInputException Throws if price is null
   * @throws PrecisionException Throws if price is not 2 decimals
   * @throws NegativeQuantityException Throws if price is negative
   * @throws InvalidNameException Throw if name is invalid
   */
  public ItemImpl(int id, String name, BigDecimal price) {
    setId(id);
    setName(name);
    setPrice(price);
  }

  /**
   * Getter for iD.
   * 
   * @return Id Returns this item's Id
   */
  @Override
  public int getId() {
    return this.id;
  }

  /**
   * Setter for Id.
   * 
   * @param id New Value for Id
   */
  @Override
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Getter for name.
   * 
   * @return Name Return's item's name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Checks name and then sets it if valid.
   * 
   * @param name Name of item.
   * @throws InvalidNameException Throw if name is invalid
   * @throws NullInputException Throw if input was null
   * @throws StringLengthException Throw if string is of invalid length
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter for price.
   * 
   * @return price Price of item
   */
  public BigDecimal getPrice() {
    return price;
  }

  /**
   * Check price if it is valid and then set.
   * 
   * @param price Price of item
   */
  @Override
  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o){
    return o.getClass() == getClass() && id == ((Item) o).getId();
  }

  @Override
  public int hashCode(){
    return id;
  }

  @Override
  public String toString() {
    return getName();
  }

}
