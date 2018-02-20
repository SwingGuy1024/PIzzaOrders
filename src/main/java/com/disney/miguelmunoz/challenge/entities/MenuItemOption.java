package com.disney.miguelmunoz.challenge.entities;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/19/18
 * <p>Time: 9:42 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Entity
public class MenuItemOption {
  private Integer id;
  private MenuItem menuItem;
  private BigDecimal deltaPrice;
  private FoodOption foodOption;

  @Id
  @GeneratedValue
  public Integer getId() {
    return id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  @ManyToOne
  @JoinColumn(name="menu_item_id")
  public MenuItem getMenuItem() {
    return menuItem;
  }

  public void setMenuItem(final MenuItem menuItem) {
    this.menuItem = menuItem;
  }

  public BigDecimal getDeltaPrice() {
    return deltaPrice;
  }

  public void setDeltaPrice(final BigDecimal deltaPrice) {
    this.deltaPrice = deltaPrice;
  }

  @OneToOne
  @JoinColumn(name="food_option_id")
  public FoodOption getFoodOption() {
    return foodOption;
  }

  public void setFoodOption(final FoodOption foodOption) {
    this.foodOption = foodOption;
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) { return true; }
    if (!(o instanceof MenuItemOption)) { return false; } // implicitly checks for null

    final MenuItemOption that = (MenuItemOption) o;

    return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
  }

  @Override
  public int hashCode() {
    return getId() != null ? getId().hashCode() : 0;
  }
}