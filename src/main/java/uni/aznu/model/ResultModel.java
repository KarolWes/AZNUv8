package uni.aznu.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultModel {
    @JsonProperty("id")
    String Id;
    @JsonProperty("message")
    String Message;

    public ResultModel() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @Override
    public String toString() {
        return "{" +  Id + "|"+ Message + "}";
    }
}
