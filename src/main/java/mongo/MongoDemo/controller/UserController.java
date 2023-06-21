package mongo.MongoDemo.controller;

import mongo.MongoDemo.dto.ServerResponse;
import mongo.MongoDemo.dto.UserDto;
import mongo.MongoDemo.dto.UserListResponse;
import mongo.MongoDemo.dto.UserRequest;
import mongo.MongoDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ServerResponse<UserDto> createUser(@RequestBody UserRequest user) {
        return ServerResponse.ok(userService.saveUser( user));
    }

    @GetMapping("/user/{id}")
    public ServerResponse<UserDto> getUser(@PathVariable("id") final String id) {
        return ServerResponse.ok(userService.getUser(id));
    }

    @GetMapping("/users")
    public ServerResponse<UserListResponse> getUsers() {
        return ServerResponse.ok(userService.getUsers());
    }

    @PutMapping("/user/{id}")
    public ServerResponse<UserDto> updateUser(@PathVariable("id") final String id, @RequestBody UserRequest user) {
        return ServerResponse.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/user/{id}")
    public ServerResponse<Void> deleteUser(@PathVariable("id") final String id) {
        userService.deleteUser(id);
        return new ServerResponse<Void>(null, null);
    }

    @DeleteMapping("/users")
    public ServerResponse<Void> deleteAllUsers() {
        userService.deleteAllUsers();
        return new ServerResponse<Void>(null, null);
    }

}
