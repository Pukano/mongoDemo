package mongo.MongoDemo;

import mongo.MongoDemo.dto.ServerResponse;
import mongo.MongoDemo.dto.UserDto;
import mongo.MongoDemo.dto.UserListResponse;
import mongo.MongoDemo.dto.UserRequest;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//change junit on testNg

public class MongoDemoParametricTests extends AbstractMongoTest {

    private static final Logger logger = LoggerFactory.getLogger(MongoDemoParametricTests.class);

        @Autowired
        private ApplicationContext applicationContext;

        private TestRestTemplate restTemplate;

        @BeforeClass
        public void init() {
            restTemplate = applicationContext.getBean(TestRestTemplate.class);
        }

        @DataProvider(name = "userData")
        public Object[][] userData() {
            return new Object[][] {
                    {"Adam", "Petrovcak", "apor@sdasd.sk", "pass123"},
            };
        }

        @Test(dataProvider = "userData")
        public void createUserTest(String firstName, String lastName, String email, String password) {
            final UserRequest request = new UserRequest(firstName, lastName, email, password);
            final ParameterizedTypeReference<ServerResponse<UserDto>> responseType = new ParameterizedTypeReference<ServerResponse<UserDto>>() {};
            final ServerResponse<UserDto> response = restTemplate.exchange("/user", HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType).getBody();
            final UserDto user = response.payload();

            Assert.assertNotNull(user);
            Assert.assertEquals(firstName, user.firstName());

            //find created user
            String userId = response.payload().id();

            // Retrieve the created user by ID
            ServerResponse<UserDto> getUserResponse = restTemplate.exchange("/user/{id}", HttpMethod.GET, null,
                    new ParameterizedTypeReference<ServerResponse<UserDto>>() {
                    }, userId).getBody();
            assertNotNull(getUserResponse);
            assertNotNull(getUserResponse.payload());
            assertEquals("Adam", getUserResponse.payload().firstName());
        }

    @Test(dataProvider = "userData")
    public void updateUserTest(String firstName, String lastName, String email, String password) {
        //create user for test
        final UserRequest requestCreate = new UserRequest(firstName, lastName, email, password);
        final ParameterizedTypeReference<ServerResponse<UserDto>> responseTypeCreate = new ParameterizedTypeReference<ServerResponse<UserDto>>() {};
        final ServerResponse<UserDto> responseCreate = restTemplate.exchange("/user", HttpMethod.POST,
                new HttpEntity<>(requestCreate), responseTypeCreate).getBody();
        final UserDto userCreate = responseCreate.payload();

        Assert.assertNotNull(userCreate);
        Assert.assertEquals(firstName, userCreate.firstName());

        String userId = userCreate.id();

        //update user
        final UserRequest updateRequest = new UserRequest("Joe", "Doe", "jdoe@sdasd.sk", "pass321");
        final ParameterizedTypeReference<ServerResponse<UserDto>> responseTypeUpdate = new ParameterizedTypeReference<ServerResponse<UserDto>>() {};
        final ServerResponse<UserDto> responseUpdate = restTemplate.exchange("/user/{id}", HttpMethod.PUT,
                new HttpEntity<>(updateRequest), responseTypeUpdate, userId).getBody();

        //check updated user data
        assertNotNull(responseUpdate);
        assertNotNull(responseUpdate.payload());
        assertEquals("Joe", responseUpdate.payload().firstName());

        // Retrieve the updated user by ID
        ServerResponse<UserDto> getUserResponse = restTemplate.exchange("/user/{id}", HttpMethod.GET, null,
                new ParameterizedTypeReference<ServerResponse<UserDto>>() {}, userId).getBody();
        assertNotNull(getUserResponse);
        assertNotNull(getUserResponse.payload());
        assertEquals("Joe", getUserResponse.payload().firstName());
        assertEquals("Doe", getUserResponse.payload().lastName());

        //delete user by id REST
        ServerResponse<UserDto> deleteUser = restTemplate.exchange("/user/{id}", HttpMethod.DELETE, null,
                new ParameterizedTypeReference<ServerResponse<UserDto>>() {
                }, userId).getBody();

        //get users and check if deleted
        ServerResponse<UserDto> getDeleteUser = restTemplate.exchange("/user/{id}", HttpMethod.GET, null,
                new ParameterizedTypeReference<ServerResponse<UserDto>>() {
                }, userId).getBody();

        // Check if the response status is Not Found
        assertEquals("user not found", getDeleteUser.error().message());

        //        Clean up, endpoint delete all
        deleteAll();
    }

    public void deleteAll() {
        ServerResponse<UserListResponse> deleteAllUsers = restTemplate.exchange("/users", HttpMethod.DELETE, null, new ParameterizedTypeReference<ServerResponse<UserListResponse>>() {
        }).getBody();
    }

}