package com.disney.miguelmunoz.challenge.entities;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/19/18
 * <p>Time: 10:49 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Entity
public class FoodOrder {
  private Integer id;
  private List<MenuItemOption> menuItemOptionList = new LinkedList<>();
  private Boolean complete = Boolean.FALSE;
  private BigDecimal finalPrice;
  private Date orderTime;
  private Date completeTime;

  @Id
  public Integer getId() {
    return id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  @ManyToMany
  @JoinTable(name="food_order_to_menu_item_option", 
      joinColumns = { @JoinColumn(name="food_order_id") },
      inverseJoinColumns = {@JoinColumn(name="menu_item_option_id") }
  )
  public List<MenuItemOption> getMenuItemOptionList() {
    return menuItemOptionList;
  }

  public void setMenuItemOptionList(final List<MenuItemOption> menuItemOptionList) {
    this.menuItemOptionList = menuItemOptionList;
  }

  public Boolean getComplete() {
    return complete;
  }

  public void setComplete(final Boolean complete) {
    this.complete = complete;
  }

  public BigDecimal getFinalPrice() {
    return finalPrice;
  }

  public void setFinalPrice(final BigDecimal finalPrice) {
    this.finalPrice = finalPrice;
  }

  public Date getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(final Date orderTime) {
    this.orderTime = orderTime;
  }

  public Date getCompleteTime() {
    return completeTime;
  }

  public void setCompleteTime(final Date completeTime) {
    this.completeTime = completeTime;
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) { return true; }
    if (!(o instanceof FoodOrder)) { return false; } // implicitly checks for null

    final FoodOrder foodOrder = (FoodOrder) o;

    return (getId() != null) ? getId().equals(foodOrder.getId()) : (foodOrder.getId() == null);
  }

  @Override
  public int hashCode() {
    return (getId() != null) ? getId().hashCode() : 0;
  }
}
