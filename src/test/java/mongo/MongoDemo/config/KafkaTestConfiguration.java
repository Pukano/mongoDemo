package mongo.MongoDemo.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

import javax.annotation.PostConstruct;

@Configuration
@EnableKafka
@Import({KafkaProducerConfiguration.class})
public class KafkaTestConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTestConfiguration.class);

    @PostConstruct
    public void init() {
        LOGGER.info("KafkaTestConfiguration INIT");
    }
}
