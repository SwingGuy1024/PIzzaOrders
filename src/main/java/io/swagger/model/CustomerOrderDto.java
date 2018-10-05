package io.swagger.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

/**
 * CustomerOrderDto
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-05T10:50:14.597Z")

public class CustomerOrderDto   {
  @JsonProperty("menuItem")
  private MenuItemDto menuItem = null;

  @JsonProperty("options")
  @Valid
  private List<MenuItemOptionDto> options = null;

  @JsonProperty("complete")
  private Boolean complete = null;

  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("orderTime")
  private OffsetDateTime orderTime = null;

  @JsonProperty("completeTime")
  private OffsetDateTime completeTime = null;

  public CustomerOrderDto menuItem(MenuItemDto menuItem) {
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

  public CustomerOrderDto options(List<MenuItemOptionDto> options) {
    this.options = options;
    return this;
  }

  public CustomerOrderDto addOptionsItem(MenuItemOptionDto optionsItem) {
    if (this.options == null) {
      this.options = new ArrayList<>();
    }
    this.options.add(optionsItem);
    return this;
  }

   /**
   * Get options
   * @return options
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<MenuItemOptionDto> getOptions() {
    return options;
  }

  public void setOptions(List<MenuItemOptionDto> options) {
    this.options = options;
  }

  public CustomerOrderDto complete(Boolean complete) {
    this.complete = complete;
    return this;
  }

   /**
   * Order complete
   * @return complete
  **/
  @ApiModelProperty(value = "Order complete")


  public Boolean isComplete() {
    return complete;
  }

  public void setComplete(Boolean complete) {
    this.complete = complete;
  }

  public CustomerOrderDto id(Integer id) {
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

  public CustomerOrderDto orderTime(OffsetDateTime orderTime) {
    this.orderTime = orderTime;
    return this;
  }

   /**
   * Get orderTime
   * @return orderTime
  **/
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(OffsetDateTime orderTime) {
    this.orderTime = orderTime;
  }

  public CustomerOrderDto completeTime(OffsetDateTime completeTime) {
    this.completeTime = completeTime;
    return this;
  }

   /**
   * Get completeTime
   * @return completeTime
  **/
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getCompleteTime() {
    return completeTime;
  }

  public void setCompleteTime(OffsetDateTime completeTime) {
    this.completeTime = completeTime;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CustomerOrderDto customerOrderDto = (CustomerOrderDto) o;
    return Objects.equals(this.menuItem, customerOrderDto.menuItem) &&
        Objects.equals(this.options, customerOrderDto.options) &&
        Objects.equals(this.complete, customerOrderDto.complete) &&
        Objects.equals(this.id, customerOrderDto.id) &&
        Objects.equals(this.orderTime, customerOrderDto.orderTime) &&
        Objects.equals(this.completeTime, customerOrderDto.completeTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(menuItem, options, complete, id, orderTime, completeTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CustomerOrderDto {\n");
    
    sb.append("    menuItem: ").append(toIndentedString(menuItem)).append("\n");
    sb.append("    options: ").append(toIndentedString(options)).append("\n");
    sb.append("    complete: ").append(toIndentedString(complete)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    orderTime: ").append(toIndentedString(orderTime)).append("\n");
    sb.append("    completeTime: ").append(toIndentedString(completeTime)).append("\n");
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

