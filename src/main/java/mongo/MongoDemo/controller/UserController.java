package mongo.MongoDemo.controller;

import mongo.MongoDemo.dto.User;
import mongo.MongoDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
    @GetMapping("/user/{id}")
    public Optional<User> getUser(@PathVariable("id") final Long id) {
        return userService.getUser(id);
    }
    @GetMapping("/users")
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }
    @PutMapping("/user/{id}")
    public User updateUser(@PathVariable("id") final Long id, @RequestBody User user) {
        return userService.saveUser(user);
    }
    @DeleteMapping("/delete")
    public void deleteUser(@PathVariable("id") final Long id) {
        userService.deleteUser(id);
    }

}
