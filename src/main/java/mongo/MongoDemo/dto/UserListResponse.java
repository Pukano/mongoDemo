package mongo.MongoDemo.dto;

import java.util.List;

public record UserListResponse(
       List<UserDto> users) {

}
