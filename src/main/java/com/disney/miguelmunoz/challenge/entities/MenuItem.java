package com.disney.miguelmunoz.challenge.entities;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
  private Collection<MenuItemOption> menuItemOptionList = new LinkedList<>();

  @Id
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
  public Collection<MenuItemOption> getMenuItemOptionList() {
    return menuItemOptionList;
  }

  public void setMenuItemOptionList(final Collection<MenuItemOption> menuItemOptionList) {
    if (menuItemOptionList == null) {
      this.menuItemOptionList = new LinkedList<>();
    } else {
      this.menuItemOptionList = menuItemOptionList;
    }
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) { return true; }
    if (!(o instanceof MenuItem)) { return false; } // implicitly checks for null

    final MenuItem menuItem = (MenuItem) o;

    return (getId() != null) ? getId().equals(menuItem.getId()) : (menuItem.getId() == null);
  }

  @Override
  public int hashCode() {
    return (getId() != null) ? getId().hashCode() : 0;
  }
}
