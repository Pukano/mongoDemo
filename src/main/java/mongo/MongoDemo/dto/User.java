package mongo.MongoDemo.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public record User(
        @Id
       Long id,
       String firstName,
       String lastName,
       String email,
       String password) {

       public User withFirstName(String firstName) {
              return new User(id, firstName, lastName, email, password);
       }

       public User withLastName(String lastName) {
              return new User(id, firstName, lastName, email, password);
       }

       public User withEmail(String email) {
              return new User(id, firstName, lastName, email, password);
       }

       public User withPassword(String password) {
              return new User(id, firstName, lastName, email, password);
       }
}
