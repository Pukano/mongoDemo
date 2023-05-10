package mongo.MongoDemo;

import mongo.MongoDemo.dto.UserDto;
import mongo.MongoDemo.dto.UserRequest;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

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
        //create user and get all stored
    void getSaveMessages() {
        final UserRequest request = new UserRequest("Adam", "Petrovcak", "apor@sdasd.sk", "pass123");
        final ResponseEntity<UserDto> responseCreate = restTemplate.postForEntity("/user", request, UserDto.class);

        assertNotNull(responseCreate.getBody());
        assertNotNull(responseCreate.getBody().id());
        assertEquals("Adam", responseCreate.getBody().firstName());


    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        static int DOCKER_EXPOSED_MONGO_PORT = 27017;
        @Container
        static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.5"))
                .withExposedPorts(DOCKER_EXPOSED_MONGO_PORT);

        private static void startContainers() {
            Startables.deepStart(Stream.of(mongoDBContainer)).join();
            assertEquals(mongoDBContainer.isRunning(), true);
            logger.info("mongoDB container is running");
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

}