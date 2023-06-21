package mongo.MongoDemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import mongo.MongoDemo.dto.*;
import mongo.MongoDemo.service.impl.MongoResourcesProducer;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;


import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@ActiveProfiles("test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = MongoDemoApplicationTests.Initializer.class)
public class MongoDemoApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(MongoDemoApplicationTests.class);
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MongoResourcesProducer mongoResourcesProducer;

    @BeforeAll
    public static void setUp() {
        System.setProperty("embedded.mongodb.started.wait.time", "60000");
    }

    @Test
    @Order(1)
    //basic test
    public void testAppContext() {
        assertNotNull(applicationContext);
    }

    @Test
    @Order(2)
// create user
    void getSaveMessages() {
        final UserRequest request = new UserRequest("Adam", "Petrovcak", "apor@sdasd.sk", "pass123");
        final ParameterizedTypeReference<ServerResponse<UserDto>> responseType = new ParameterizedTypeReference<ServerResponse<UserDto>>() {
        };
        final ServerResponse<UserDto> responseObject = restTemplate.exchange("/user", HttpMethod.POST,
                new HttpEntity<>(request),
                responseType).getBody();

        assertNotNull(responseObject.payload());
        assertNotNull(responseObject.payload().id());
        assertEquals("Adam", responseObject.payload().firstName());

        //    get stored users
        ServerResponse<UserListResponse> responseGetAll = restTemplate.exchange("/users", HttpMethod.GET, null,
                new ParameterizedTypeReference<ServerResponse<UserListResponse>>() {
                }).getBody();
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.payload());
        assertEquals(1, responseGetAll.payload().users().size());
        assertEquals("Petrovcak", responseGetAll.payload().users().get(0).lastName());

        //find created user
        String userId = responseGetAll.payload().users().get(0).id();

        // Retrieve the created user by ID
        ServerResponse<UserDto> getUserResponse = restTemplate.exchange("/user/{id}", HttpMethod.GET, null,
                new ParameterizedTypeReference<ServerResponse<UserDto>>() {
                }, userId).getBody();
        assertNotNull(getUserResponse);
        assertNotNull(getUserResponse.payload());
        assertEquals("Adam", getUserResponse.payload().firstName());

        //update user REST call
        final UserRequest updateRequest = new UserRequest("Joe", "Doe", "jdoe@sdasd.sk", "pass321");
        final ParameterizedTypeReference<ServerResponse<UserDto>> response = new ParameterizedTypeReference<ServerResponse<UserDto>>() {
        };
        final ServerResponse<UserDto> responseCreate = restTemplate.exchange("/user/{id}", HttpMethod.PUT,
                new HttpEntity<>(updateRequest), response, userId).getBody();
        assertNotNull(responseCreate);
        assertNotNull(responseCreate.payload());
        assertEquals("Joe", responseCreate.payload().firstName());

        //get user by id REST
        ServerResponse<UserDto> getUserById = restTemplate.exchange("/user/{id}", HttpMethod.GET, null,
                new ParameterizedTypeReference<ServerResponse<UserDto>>() {
                }, userId).getBody();
        assertNotNull(getUserById);
        assertNotNull(getUserById.payload());
        assertEquals("Doe", getUserById.payload().lastName());
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

//        Clean up zmazat vsetko z monga; mozno aj novy endpoint delete all
        deleteAll();
    }

    @Test
    @Order(3)
// create user and get all stored
    void kafkaSaveMessages() throws InterruptedException {
        //create user kafka OR REST
        final UserRequest request = new UserRequest("Adam", "Petrovcak", "apor@sdasd.sk", "pass123");
        final UserEvent createUserEvent = new UserEvent(EventType.CREATE_USER, request, null);
        mongoResourcesProducer.send(createUserEvent);
        Thread.sleep(1000);

        //    get stored users
        ServerResponse<UserListResponse> responseGetAll = restTemplate.exchange("/users", HttpMethod.GET, null,
                new ParameterizedTypeReference<ServerResponse<UserListResponse>>() {
        }).getBody();
        assertNotNull(responseGetAll);
        assertNotNull(responseGetAll.payload());
        assertEquals(1, responseGetAll.payload().users().size());
        assertEquals("Petrovcak", responseGetAll.payload().users().get(0).lastName());

        //find created user by kafka
        String userId = responseGetAll.payload().users().get(0).id();

        // Retrieve the created user by ID
        ServerResponse<UserDto> getUserResponse = restTemplate.exchange("/user/{id}", HttpMethod.GET, null,
                new ParameterizedTypeReference<ServerResponse<UserDto>>() {
                }, userId).getBody();
        assertNotNull(getUserResponse);
        assertNotNull(getUserResponse.payload());
        assertEquals("Adam", getUserResponse.payload().firstName());

        //update user REST call
        final UserRequest updateRequest = new UserRequest("Joe", "Doe", "jdoe@sdasd.sk", "pass321");
        final ParameterizedTypeReference<ServerResponse<UserDto>> responseType = new ParameterizedTypeReference<ServerResponse<UserDto>>() {
        };
        final ServerResponse<UserDto> responseCreate = restTemplate.exchange("/user/{id}", HttpMethod.PUT, new HttpEntity<>(updateRequest), responseType, userId).getBody();
        assertNotNull(responseCreate);
        assertNotNull(responseCreate.payload());
        assertEquals("Joe", responseCreate.payload().firstName());

        //get user by id REST
        ServerResponse<UserDto> getUserById = restTemplate.exchange("/user/{id}", HttpMethod.GET, null,
                new ParameterizedTypeReference<ServerResponse<UserDto>>() {
                }, userId).getBody();
        assertNotNull(getUserById);
        assertNotNull(getUserById.payload());
        assertEquals("Doe", getUserById.payload().lastName());
        //delete user by id KAFKA OR REST
//        ServerResponse<UserDto> deleteUser = restTemplate.exchange("/user/{id}", HttpMethod.DELETE, null,
//                new ParameterizedTypeReference<ServerResponse<UserDto>>() {
//                }, userId).getBody();
        final UserEvent deleteUserEvent = new UserEvent(EventType.DELETE_USER, null, userId);
        mongoResourcesProducer.send(deleteUserEvent);
        Thread.sleep(1000);

        //get users and check if deleted
        ServerResponse<UserDto> getDeleteUser = restTemplate.exchange("/user/{id}", HttpMethod.GET, null,
                new ParameterizedTypeReference<ServerResponse<UserDto>>() {
                }, userId).getBody();

    // Check if the response status is Not Found
        assertEquals("user not found", getDeleteUser.error().message());

        deleteAll();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        static int DOCKER_EXPOSED_MONGO_PORT = 27017;
        static final int DOCKER_EXPOSED_KAFKA_PORT = 9093;

        static KafkaContainer KAFKA_CONTAINER;
        @Container
        static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.5"))
                .withExposedPorts(DOCKER_EXPOSED_MONGO_PORT);

        public static String getKafkaUrl() {
            final int kafkaBoundPort = KAFKA_CONTAINER.getMappedPort(DOCKER_EXPOSED_KAFKA_PORT);
            final String kafkaIp = KAFKA_CONTAINER.getHost();

            return kafkaIp + ":" + kafkaBoundPort;
        }

        private static void startContainers() {
            Startables.deepStart(Stream.of(mongoDBContainer)).join();
            assertEquals(mongoDBContainer.isRunning(), true);
            logger.info("mongoDB container is running");
            KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"))
                    .withExposedPorts(DOCKER_EXPOSED_KAFKA_PORT);


            KAFKA_CONTAINER.start();

            MatcherAssert.assertThat(KAFKA_CONTAINER.isRunning(), is(equalTo(true)));
            logger.info("KAFKA_URL: {}", getKafkaUrl());
            System.setProperty("KAFKA_URL", getKafkaUrl());
        }

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            startContainers();

            final Integer mongoBoundPort = mongoDBContainer.getMappedPort(DOCKER_EXPOSED_MONGO_PORT);
            final String mongoIp = mongoDBContainer.getContainerIpAddress();

            TestPropertyValues.of(
//                    "spring.data.mongodb.host=" + mongoIp,
//                    "spring.data.mongodb.port=" + mongoBoundPort,
//                    "spring.data.mongodb.database=test"
                    "spring.data.mongodb.uri=mongodb://" + mongoIp + ":" + mongoBoundPort + "/test"
            ).applyTo(context.getEnvironment());
        }

    }

    public void deleteAll() {
        ServerResponse<UserListResponse> deleteAllUsers = restTemplate.exchange("/users", HttpMethod.DELETE, null, new ParameterizedTypeReference<ServerResponse<UserListResponse>>() {
        }).getBody();
    }

}