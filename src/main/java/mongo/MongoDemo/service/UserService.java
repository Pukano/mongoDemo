package mongo.MongoDemo.service;

import mongo.MongoDemo.dto.UserDto;
import mongo.MongoDemo.dto.UserListResponse;
import mongo.MongoDemo.dto.UserRequest;

public interface UserService {
    public UserDto getUser(final String id);

    public UserListResponse getUsers();

    public void deleteUser(final String id);

    public UserDto updateUser(String id, UserRequest user);
    public UserDto saveUser(UserRequest user);

}
