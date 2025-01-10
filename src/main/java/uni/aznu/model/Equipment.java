/*
 * Booking micro service
 * Micro service to book a traveling equipment
 *
 * OpenAPI spec version: 1.0.0
 * Contact: supportm@bp.org
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package uni.aznu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;


import javax.annotation.Generated;
import java.util.Objects;
/**
 * Equipment
 */

@Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2024-12-13T14:13:08.323+01:00[Europe/Belgrade]")
public class Equipment {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("eType")
  private String eType = null;

  @JsonProperty("dateFrom")
  private String dateFrom = null;

  @JsonProperty("dateTo")
  private String dateTo = null;

  @JsonProperty("state")
  private String state = null;

  public Equipment name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Equipment eType(String eType) {
    this.eType = eType;
    return this;
  }

   /**
   * Get eType
   * @return eType
  **/
  
  public String getEType() {
    return eType;
  }

  public void setEType(String eType) {
    this.eType = eType;
  }

  public Equipment dateFrom(String dateFrom) {
    this.dateFrom = dateFrom;
    return this;
  }

   /**
   * Get dateFrom
   * @return dateFrom
  **/
  
  public String getDateFrom() {
    return dateFrom;
  }

  public void setDateFrom(String dateFrom) {
    this.dateFrom = dateFrom;
  }

  public Equipment dateTo(String dateTo) {
    this.dateTo = dateTo;
    return this;
  }

   /**
   * Get dateTo
   * @return dateTo
  **/
  
  public String getDateTo() {
    return dateTo;
  }

  public void setDateTo(String dateTo) {
    this.dateTo = dateTo;
  }

  public Equipment state(String state) {
    this.state = state;
    return this;
  }

   /**
   * Get state
   * @return state
  **/
  
  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Equipment equipment = (Equipment) o;
    return Objects.equals(this.name, equipment.name) &&
        Objects.equals(this.eType, equipment.eType) &&
        Objects.equals(this.dateFrom, equipment.dateFrom) &&
        Objects.equals(this.dateTo, equipment.dateTo) &&
        Objects.equals(this.state, equipment.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, eType, dateFrom, dateTo, state);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Equipment {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    eType: ").append(toIndentedString(eType)).append("\n");
    sb.append("    dateFrom: ").append(toIndentedString(dateFrom)).append("\n");
    sb.append("    dateTo: ").append(toIndentedString(dateTo)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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
