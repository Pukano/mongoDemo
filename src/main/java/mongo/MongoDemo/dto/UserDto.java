package mongo.MongoDemo.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public record UserDto(
       Long id,
       String firstName,
       String lastName,
       String email,
       String password) {

       public UserDto withFirstName(String firstName) {
              return new UserDto(id, firstName, lastName, email, password);
       }

       public UserDto withLastName(String lastName) {
              return new UserDto(id, firstName, lastName, email, password);
       }

       public UserDto withEmail(String email) {
              return new UserDto(id, firstName, lastName, email, password);
       }

       public UserDto withPassword(String password) {
              return new UserDto(id, firstName, lastName, email, password);
       }
}
