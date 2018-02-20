package com.disney.miguelmunoz.challenge.entities;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import static com.disney.miguelmunoz.challenge.entities.PojoUtility.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/19/18
 * <p>Time: 10:49 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Entity
public class CustomerOrder {
  private Integer id;
  private List<MenuItemOption> menuItemOptionList = new LinkedList<>();
  private Boolean complete = Boolean.FALSE;
  private BigDecimal finalPrice;
  private Date orderTime;
  private Date completeTime;

  @Id
  @GeneratedValue
  public Integer getId() {
    return id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  @ManyToMany
  @JoinTable(
      name = "food_order_to_menu_item_option", 
      joinColumns = @JoinColumn(name = "food_order_id"), 
      inverseJoinColumns = @JoinColumn(name = "menu_item_option_id")
  )
  public List<MenuItemOption> getMenuItemOptionList() {
    return menuItemOptionList;
  }

  public void setMenuItemOptionList(final List<MenuItemOption> menuItemOptionList) {
    if (menuItemOptionList == null) {
      this.menuItemOptionList = new LinkedList<>();
    } else {
      this.menuItemOptionList = menuItemOptionList;
    }
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
    return cloneDate(orderTime);
  }

  public void setOrderTime(final Date orderTime) {
    this.orderTime = cloneDate(orderTime);  
  }

  public Date getCompleteTime() {
    return cloneDate(completeTime);
  }

  public void setCompleteTime(final Date completeTime) {
    this.completeTime = cloneDate(completeTime);
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) { return true; }
    if (!(o instanceof CustomerOrder)) { return false; } // implicitly checks for null

    final CustomerOrder customerOrder = (CustomerOrder) o;

    final Integer theId = getId();
    return (theId != null) ? theId.equals(customerOrder.getId()) : (customerOrder.getId() == null);
  }

  @Override
  public int hashCode() {
    return (getId() != null) ? getId().hashCode() : 0;
  }
}
