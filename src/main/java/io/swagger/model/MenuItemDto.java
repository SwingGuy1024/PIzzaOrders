package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.MenuItemOptionDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * MenuItemDto
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-09-14T10:11:39.090Z")


@JsonInclude(JsonInclude.Include.NON_NULL)

public class MenuItemDto   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("itemPrice")
  private String itemPrice = null;

  @JsonProperty("allowedOptions")
  @Valid
  private List<MenuItemOptionDto> allowedOptions = null;

  @JsonProperty("id")
  private Integer id = null;

  public MenuItemDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MenuItemDto itemPrice(String itemPrice) {
    this.itemPrice = itemPrice;
    return this;
  }

  /**
   * Floating point price. Strings are easier to work with.
   * @return itemPrice
  **/
  @ApiModelProperty(required = true, value = "Floating point price. Strings are easier to work with.")
  @NotNull


  public String getItemPrice() {
    return itemPrice;
  }

  public void setItemPrice(String itemPrice) {
    this.itemPrice = itemPrice;
  }

  public MenuItemDto allowedOptions(List<MenuItemOptionDto> allowedOptions) {
    this.allowedOptions = allowedOptions;
    return this;
  }

  public MenuItemDto addAllowedOptionsItem(MenuItemOptionDto allowedOptionsItem) {
    if (this.allowedOptions == null) {
      this.allowedOptions = new ArrayList<>();
    }
    this.allowedOptions.add(allowedOptionsItem);
    return this;
  }

  /**
   * Get allowedOptions
   * @return allowedOptions
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<MenuItemOptionDto> getAllowedOptions() {
    return allowedOptions;
  }

  public void setAllowedOptions(List<MenuItemOptionDto> allowedOptions) {
    this.allowedOptions = allowedOptions;
  }

  public MenuItemDto id(Integer id) {
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
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MenuItemDto menuItemDto = (MenuItemDto) o;
    return Objects.equals(this.name, menuItemDto.name) &&
        Objects.equals(this.itemPrice, menuItemDto.itemPrice) &&
        Objects.equals(this.allowedOptions, menuItemDto.allowedOptions) &&
        Objects.equals(this.id, menuItemDto.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, itemPrice, allowedOptions, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MenuItemDto {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    itemPrice: ").append(toIndentedString(itemPrice)).append("\n");
    sb.append("    allowedOptions: ").append(toIndentedString(allowedOptions)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

