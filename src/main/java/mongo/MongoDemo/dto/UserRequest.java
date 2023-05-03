package mongo.MongoDemo.dto;

public record UserRequest(
       String firstName,
       String lastName,
       String email,
       String password) {
}
