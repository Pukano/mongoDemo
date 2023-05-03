package mongo.MongoDemo.mapper;

import mongo.MongoDemo.document.UserDocument;
import mongo.MongoDemo.dto.UserDto;
import mongo.MongoDemo.dto.UserListResponse;
import mongo.MongoDemo.dto.UserRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto toDto(UserDocument userDocument){
        return new UserDto(userDocument.getId(), userDocument.getFirstName(), userDocument.getLastName(), userDocument.getEmail());
    }

    public UserListResponse toListResponse(List<UserDocument> userDocuments){
        final List<UserDto> userDtos = userDocuments.stream()
                .map(userDocument -> toDto(userDocument))
                .collect(Collectors.toList());

        return new UserListResponse(userDtos);
    }
    public UserDocument dtoToDocument(UserDto userDto){
        return new UserDocument(userDto.id(), userDto.firstName(), userDto.lastName(), userDto.email(), null);
    }
    public UserDocument userRequestToDocument(UserRequest userRequest){
        return new UserDocument(null, userRequest.firstName(), userRequest.lastName(), userRequest.email(), userRequest.password());
    }
}
