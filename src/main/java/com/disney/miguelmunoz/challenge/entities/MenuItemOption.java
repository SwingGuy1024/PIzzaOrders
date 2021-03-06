package com.disney.miguelmunoz.challenge.entities;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
  private String name;
  private Collection<CustomerOrder> orders;

  @Id
  @GeneratedValue
  public Integer getId() {
    return id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  @JsonIgnore
  @ManyToOne(cascade = CascadeType.REMOVE)
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

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  /**
   * There is no practical reason for this method. It's only here because Hibernate can't manage a single-sided 
   * Many-To-Many relationship. The other side is essential, but this side is only here to make the other side work.
   * <p/>
   * Also, this is annotated with @Fetch to prevent error messages that say this: 
   * "org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags"
   * See https://stackoverflow.com/questions/4334970/hibernate-cannot-simultaneously-fetch-multiple-bags
   * @return Don't worry about it.
   * 
   */
  @Fetch(value = FetchMode.SUBSELECT)
  @JsonIgnore
  @ManyToMany
  @JoinTable(
      name = "food_order_to_menu_item_option",
      inverseJoinColumns = @JoinColumn(name = "food_order_id"),
      joinColumns = @JoinColumn(name = "menu_item_option_id")
  )
  public Collection<CustomerOrder> getOrders() {
    return orders;
  }

  public void setOrders(final Collection<CustomerOrder> orders) {
    if (orders == null) {
      this.orders = new LinkedList<>();
    } else {
      this.orders = orders;
    }
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) { return true; }
    if (!(o instanceof MenuItemOption)) { return false; } // implicitly checks for null

    final MenuItemOption that = (MenuItemOption) o;

    return (getId() != null) ? getId().equals(that.getId()) : (that.getId() == null);
  }

  @Override
  public int hashCode() {
    return (getId() != null) ? getId().hashCode() : 0;
  }

  @Override
  public String toString() {
    //noinspection StringConcatenation,ObjectToString,MagicCharacter
    return "MenuItemOption{" +
        "id=" + id +
        ", menuItem=" + menuItem +
        ", deltaPrice=" + deltaPrice +
        ", name='" + name + '\'' +
        '}';
  }
}
