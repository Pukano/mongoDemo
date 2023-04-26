package mongo.MongoDemo.service;

import mongo.MongoDemo.document.UserDocument;
import java.util.Optional;

public interface UserService {
    public Optional<UserDocument> getUser(final Long id);

    public Iterable<UserDocument> getUsers();

    public void deleteUser(final Long id);

    public UserDocument saveUser(UserDocument user);

}
