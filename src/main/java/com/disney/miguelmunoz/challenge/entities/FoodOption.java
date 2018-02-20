package com.disney.miguelmunoz.challenge.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/19/18
 * <p>Time: 9:32 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Entity
public class FoodOption {
  private Integer id;
  private String name;

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

  @Override
  public boolean equals(final Object o) {

    if (this == o) { return true; }
    if (!(o instanceof FoodOption)) { return false; }

    final FoodOption that = (FoodOption) o;

    return (getName() != null) ? getName().equals(that.getName()) : (that.getName() == null);
  }

  @Override
  public int hashCode() {
    return (getName() != null) ? getName().hashCode() : 0;
  }

  //  @Override
//  public boolean equals(final Object o) {
//    if (this == o) return true;
//    if (o instanceof FoodOption) {
//      final FoodOption that = (FoodOption) o;
//      return Objects.equals(getName(), that.getName());
//    }
//    return false;
//  }
//
//  @Override
//  public int hashCode() {
//
//    return Objects.hash(getName());
//  }
}
