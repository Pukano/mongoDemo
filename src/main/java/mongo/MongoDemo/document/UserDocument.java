package mongo.MongoDemo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "users")
public class UserDocument{

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    public UserDocument() {
    }

    public UserDocument(String id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
