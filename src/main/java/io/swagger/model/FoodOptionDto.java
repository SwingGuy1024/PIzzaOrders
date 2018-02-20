package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FoodOptionDto
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T09:31:20.427Z")

public class FoodOptionDto   {
  @JsonProperty("option")
  private String option = null;

  @JsonProperty("id")
  private Integer id = null;

  public FoodOptionDto option(String option) {
    this.option = option;
    return this;
  }

   /**
   * Option for food, such as no cheese or pepperoni. The table is just a modifieable enumeration.
   * @return option
  **/
  @ApiModelProperty(required = true, value = "Option for food, such as no cheese or pepperoni. The table is just a modifieable enumeration.")
  @NotNull


  public String getOption() {
    return option;
  }

  public void setOption(String option) {
    this.option = option;
  }

  public FoodOptionDto id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


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
    FoodOptionDto foodOptionDto = (FoodOptionDto) o;
    return Objects.equals(this.option, foodOptionDto.option) &&
        Objects.equals(this.id, foodOptionDto.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(option, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FoodOptionDto {\n");
    
    sb.append("    option: ").append(toIndentedString(option)).append("\n");
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

