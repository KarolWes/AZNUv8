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


import javax.annotation.Generated;
import java.util.Objects;
/**
 * Visit
 */

@Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2024-12-13T14:13:08.323+01:00[Europe/Belgrade]")
public class Visit {
  @JsonProperty("date")
  private String date = null;

  @JsonProperty("vType")
  private String vType = null;

  public Visit date(String date) {
    this.date = date;
    return this;
  }

   /**
   * Get date
   * @return date
  **/
  
  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Visit vType(String vType) {
    this.vType = vType;
    return this;
  }

   /**
   * Get vType
   * @return vType
  **/
  
  public String getVType() {
    return vType;
  }

  public void setVType(String vType) {
    this.vType = vType;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Visit visit = (Visit) o;
    return Objects.equals(this.date, visit.date) &&
        Objects.equals(this.vType, visit.vType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, vType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Visit {\n");
    
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    vType: ").append(toIndentedString(vType)).append("\n");
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
