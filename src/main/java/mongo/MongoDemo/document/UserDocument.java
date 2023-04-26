package mongo.MongoDemo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public record UserDocument(
        @Id
       Long id,
       String firstName,
       String lastName,
       String email,
       String password) {

       public UserDocument withFirstName(String firstName) {
              return new UserDocument(id, firstName, lastName, email, password);
       }

       public UserDocument withLastName(String lastName) {
              return new UserDocument(id, firstName, lastName, email, password);
       }

       public UserDocument withEmail(String email) {
              return new UserDocument(id, firstName, lastName, email, password);
       }

       public UserDocument withPassword(String password) {
              return new UserDocument(id, firstName, lastName, email, password);
       }
}
