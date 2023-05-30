package mongo.MongoDemo.dto;

public record UserEvent(EventType eventType, UserRequest userRequest, String id) {
}
