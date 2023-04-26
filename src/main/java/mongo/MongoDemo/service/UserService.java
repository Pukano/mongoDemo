package mongo.MongoDemo.service;

import mongo.MongoDemo.dto.User;
import java.util.Optional;

public interface UserService {
    public Optional<User> getUser(final Long id);

    public Iterable<User> getUsers();

    public void deleteUser(final Long id);

    public User saveUser(User user);

}
