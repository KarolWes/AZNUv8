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
 * BookProcessRequest
 */

@Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2024-12-13T14:13:08.323+01:00[Europe/Belgrade]")
public class BookProcessRequest {
  @JsonProperty("person")
  private Person person = null;

  @JsonProperty("equipment")
  private Equipment equipment = null;

  @JsonProperty("visit")
  private Visit visit = null;

  @JsonProperty("paymentCard")
  private PaymentCard paymentCard = null;

  public BookProcessRequest person(Person person) {
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

  public BookProcessRequest equipment(Equipment equipment) {
    this.equipment = equipment;
    return this;
  }

   /**
   * Get equipment
   * @return equipment
  **/
  
  public Equipment getEquipment() {
    return equipment;
  }

  public void setEquipment(Equipment equipment) {
    this.equipment = equipment;
  }

  public BookProcessRequest visit(Visit visit) {
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

  public BookProcessRequest paymentCard(PaymentCard paymentCard) {
    this.paymentCard = paymentCard;
    return this;
  }

   /**
   * Get paymentCard
   * @return paymentCard
  **/
  
  public PaymentCard getPaymentCard() {
    return paymentCard;
  }

  public void setPaymentCard(PaymentCard paymentCard) {
    this.paymentCard = paymentCard;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookProcessRequest bookProcessRequest = (BookProcessRequest) o;
    return Objects.equals(this.person, bookProcessRequest.person) &&
        Objects.equals(this.equipment, bookProcessRequest.equipment) &&
        Objects.equals(this.visit, bookProcessRequest.visit) &&
        Objects.equals(this.paymentCard, bookProcessRequest.paymentCard);
  }

  @Override
  public int hashCode() {
    return Objects.hash(person, equipment, visit, paymentCard);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookProcessRequest {\n");
    
    sb.append("    person: ").append(toIndentedString(person)).append("\n");
    sb.append("    equipment: ").append(toIndentedString(equipment)).append("\n");
    sb.append("    visit: ").append(toIndentedString(visit)).append("\n");
    sb.append("    paymentCard: ").append(toIndentedString(paymentCard)).append("\n");
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
