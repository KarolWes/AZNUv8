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
 * BookVisitRequest
 */

@Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2024-12-13T14:13:08.323+01:00[Europe/Belgrade]")
public class BookVisitRequest {
  @JsonProperty("person")
  private Person person = null;

  @JsonProperty("visit")
  private Visit visit = null;

  public BookVisitRequest person(Person person) {
    this.person = person;
    return this;
  }

   /**
   * Get person
   * @return person
  **/
  
  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public BookVisitRequest visit(Visit visit) {
    this.visit = visit;
    return this;
  }

   /**
   * Get visit
   * @return visit
  **/
  
  public Visit getVisit() {
    return visit;
  }

  public void setVisit(Visit visit) {
    this.visit = visit;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookVisitRequest bookVisitRequest = (BookVisitRequest) o;
    return Objects.equals(this.person, bookVisitRequest.person) &&
        Objects.equals(this.visit, bookVisitRequest.visit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(person, visit);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookVisitRequest {\n");
    
    sb.append("    person: ").append(toIndentedString(person)).append("\n");
    sb.append("    visit: ").append(toIndentedString(visit)).append("\n");
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
