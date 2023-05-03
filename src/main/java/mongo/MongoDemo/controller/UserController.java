package mongo.MongoDemo.controller;

import mongo.MongoDemo.dto.UserDto;
import mongo.MongoDemo.dto.UserListResponse;
import mongo.MongoDemo.dto.UserRequest;
import mongo.MongoDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseEntity<UserDto> createUser(@RequestBody UserRequest user) {
        return ResponseEntity.ok(userService.saveUser( user));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") final String id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/users")
    public ResponseEntity<UserListResponse> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") final String id, @RequestBody UserRequest user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/delete")
    public void deleteUser(@PathVariable("id") final String id) {
        userService.deleteUser(id);
    }

}
