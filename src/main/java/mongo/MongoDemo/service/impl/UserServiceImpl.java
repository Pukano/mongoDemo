package mongo.MongoDemo.service.impl;

import mongo.MongoDemo.dto.User;
import mongo.MongoDemo.repository.UserRepository;
import mongo.MongoDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUser(final Long id) {
        return userRepository.findById(id);
    }

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(final Long id) {
        userRepository.deleteById(id);
    }

    public User saveUser(User user) {
        Optional<User> usr = userRepository.findById(user.id());
        if (usr.isPresent()) {
            User currentUser = usr.get();

            String firstName = user.firstName();
            if (firstName != null) {
                currentUser = currentUser.withFirstName(firstName);
            }

            String lastName = user.lastName();
            if (lastName != null) {
                currentUser = currentUser.withLastName(lastName);
            }

            String email = user.email();
            if (email != null) {
                currentUser = currentUser.withEmail(email);
            }

            String password = user.password();
            if (password != null) {
                currentUser = currentUser.withPassword(password);
            }

            return userRepository.save(currentUser);
        } else {
            return null;
        }
    }
}
