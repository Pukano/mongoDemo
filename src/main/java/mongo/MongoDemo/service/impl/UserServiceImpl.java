package mongo.MongoDemo.service.impl;

import mongo.MongoDemo.document.UserDocument;
import mongo.MongoDemo.dto.UserDto;
import mongo.MongoDemo.dto.UserListResponse;
import mongo.MongoDemo.dto.UserRequest;
import mongo.MongoDemo.exception.MongoDemoException;
import mongo.MongoDemo.mapper.UserMapper;
import mongo.MongoDemo.repository.UserRepository;
import mongo.MongoDemo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto getUser(final String id) {
        final UserDocument userDocument = userRepository.findById(id)
                .orElseThrow(() -> new MongoDemoException("user not found", "c01"));
        return userMapper.toDto(userDocument);
    }

    public UserListResponse getUsers() {
        return userMapper.toListResponse(userRepository.findAll());
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public UserDto updateUser(String id, UserRequest user) {
        final Optional<UserDocument> usr = userRepository.findById(id);
        if (usr.isPresent()) {
            UserDocument currentUser = usr.get();
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

            final UserDocument saved = userRepository.save(currentUser);
            return userMapper.toDto(saved);
        } else {
            throw new MongoDemoException("user with ID:" + id + " doesn't exists", "c02");
        }
    }

    public UserDto saveUser(UserRequest user) {
        final UserDocument userDocument = userMapper.userRequestToDocument(user);
        final UserDocument saved = userRepository.save(userDocument);

        return userMapper.toDto(saved);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
}
