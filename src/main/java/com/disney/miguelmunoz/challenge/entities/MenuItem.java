package com.disney.miguelmunoz.challenge.entities;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/19/18
 * <p>Time: 10:43 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Entity
public class MenuItem {
  private Integer id;
  private String name;
  private BigDecimal itemPrice;
  private Collection<MenuItemOption> allowedOptions = new LinkedList<>();

  @Id
  @GeneratedValue
  public Integer getId() {
    return id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public BigDecimal getItemPrice() {
    return itemPrice;
  }

  public void setItemPrice(final BigDecimal itemPrice) {
    this.itemPrice = itemPrice;
  }

  @OneToMany(mappedBy = "menuItem", targetEntity = MenuItemOption.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  public Collection<MenuItemOption> getAllowedOptions() {
    return allowedOptions;
  }

  public void setAllowedOptions(final Collection<MenuItemOption> allowedOptions) {
    if (allowedOptions == null) {
      this.allowedOptions = new LinkedList<>();
    } else {
      this.allowedOptions = allowedOptions;
    }
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) { return true; }
    if (!(o instanceof MenuItem)) { return false; } // implicitly checks for null

    final MenuItem menuItem = (MenuItem) o;
    
    if (getId() == null) {
      //noinspection ProhibitedExceptionThrown
      throw new NullPointerException("Not yet persisted Entity: Never call equals() or add to a Set");
    }

    return getId().equals(menuItem.getId());
  }

  @Override
  public int hashCode() {
    return (getId() != null) ? getId().hashCode() : 0;
  }
}
