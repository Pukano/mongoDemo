package mongo.MongoDemo;

import kafka.MongoResourcesProducer;
import mongo.MongoDemo.dto.*;
import org.hamcrest.MatcherAssert;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
// create user and get all stored
    void getSaveMessages() {
        final UserRequest request = new UserRequest("Adam", "Petrovcak", "apor@sdasd.sk", "pass123");
        final ParameterizedTypeReference<ServerResponse<UserDto>> responseType = new ParameterizedTypeReference<ServerResponse<UserDto>>() {};

        final ServerResponse<UserDto> responseCreate = restTemplate.exchange("/user", HttpMethod.POST, new HttpEntity<>(request), responseType).getBody();

        assertNotNull(responseCreate.payload());
        assertNotNull(responseCreate.payload().id());
        assertEquals("Adam", responseCreate.payload().firstName());
    }
    @Test
    @Order(3)
// create user and get all stored
    void kafkaSaveMessages() {
        final UserRequest request = new UserRequest("Adam", "Petrovcak", "apor@sdasd.sk", "pass123");
        final UserEvent createUserEvent =  new UserEvent(EventType.CREATE_USER, request, null);
        mongoResourcesProducer.send(createUserEvent);
        //get stored user

        /*final ParameterizedTypeReference<ServerResponse<UserDto>> responseType = new ParameterizedTypeReference<ServerResponse<UserDto>>() {};

        final ServerResponse<UserDto> responseCreate = restTemplate.exchange("/user", HttpMethod.POST, new HttpEntity<>(request), responseType).getBody();

        assertNotNull(responseCreate.payload());
        assertNotNull(responseCreate.payload().id());
        assertEquals("Adam", responseCreate.payload().firstName());*/
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

}