package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.FoodOptionDto;
import io.swagger.model.MenuItemDto;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Links a FoodOption to a MenuItem
 */
@ApiModel(description = "Links a FoodOption to a MenuItem")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T09:31:20.427Z")

public class MenuItemOptionDto   {
  @JsonProperty("foodOption")
  private FoodOptionDto foodOption = null;

  @JsonProperty("menuItem")
  private MenuItemDto menuItem = null;

  @JsonProperty("deltaPrice")
  private String deltaPrice = null;

  @JsonProperty("id")
  private Integer id = null;

  public MenuItemOptionDto foodOption(FoodOptionDto foodOption) {
    this.foodOption = foodOption;
    return this;
  }

   /**
   * Get foodOption
   * @return foodOption
  **/
  @ApiModelProperty(value = "")

  @Valid

  public FoodOptionDto getFoodOption() {
    return foodOption;
  }

  public void setFoodOption(FoodOptionDto foodOption) {
    this.foodOption = foodOption;
  }

  public MenuItemOptionDto menuItem(MenuItemDto menuItem) {
    this.menuItem = menuItem;
    return this;
  }

   /**
   * Get menuItem
   * @return menuItem
  **/
  @ApiModelProperty(value = "")

  @Valid

  public MenuItemDto getMenuItem() {
    return menuItem;
  }

  public void setMenuItem(MenuItemDto menuItem) {
    this.menuItem = menuItem;
  }

  public MenuItemOptionDto deltaPrice(String deltaPrice) {
    this.deltaPrice = deltaPrice;
    return this;
  }

   /**
   * floating point price. Strings are easier to work with.
   * @return deltaPrice
  **/
  @ApiModelProperty(value = "floating point price. Strings are easier to work with.")


  public String getDeltaPrice() {
    return deltaPrice;
  }

  public void setDeltaPrice(String deltaPrice) {
    this.deltaPrice = deltaPrice;
  }

  public MenuItemOptionDto id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MenuItemOptionDto menuItemOptionDto = (MenuItemOptionDto) o;
    return Objects.equals(this.foodOption, menuItemOptionDto.foodOption) &&
        Objects.equals(this.menuItem, menuItemOptionDto.menuItem) &&
        Objects.equals(this.deltaPrice, menuItemOptionDto.deltaPrice) &&
        Objects.equals(this.id, menuItemOptionDto.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(foodOption, menuItem, deltaPrice, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MenuItemOptionDto {\n");
    
    sb.append("    foodOption: ").append(toIndentedString(foodOption)).append("\n");
    sb.append("    menuItem: ").append(toIndentedString(menuItem)).append("\n");
    sb.append("    deltaPrice: ").append(toIndentedString(deltaPrice)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

