package mongo.MongoDemo.controller;

import mongo.MongoDemo.document.UserDocument;
import mongo.MongoDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public UserDocument createUser(@RequestBody UserDocument user) {
        return userService.saveUser(user);
    }
    @GetMapping("/user/{id}")
    public Optional<UserDocument> getUser(@PathVariable("id") final Long id) {
        return userService.getUser(id);
    }
    @GetMapping("/users")
    public Iterable<UserDocument> getUsers() {
        return userService.getUsers();
    }
    @PutMapping("/user/{id}")
    public UserDocument updateUser(@PathVariable("id") final Long id, @RequestBody UserDocument user) {
        return userService.saveUser(user);
    }
    @DeleteMapping("/delete")
    public void deleteUser(@PathVariable("id") final Long id) {
        userService.deleteUser(id);
    }

}
