package mongo.MongoDemo;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
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

//change junit on testNg


@ActiveProfiles("test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AbstractMongoTest.Initializer.class)
public class AbstractMongoTest extends AbstractTestNGSpringContextTests {
    private static final Logger logger = LoggerFactory.getLogger(MongoDemoApplicationTests.class);

    @Autowired
    TestRestTemplate restTemplate ;

    @BeforeAll
    public static void setUp() {
        System.setProperty("embedded.mongodb.started.wait.time", "60000");
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
