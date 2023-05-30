package mongo.MongoDemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public record UserDto(
       @JsonProperty("id")
        String id,
       @JsonProperty("firstName")
       String firstName,
       @JsonProperty("lastName")
       String lastName,
       @JsonProperty("email")
       String email) {

}
