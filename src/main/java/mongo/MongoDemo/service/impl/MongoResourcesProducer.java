package mongo.MongoDemo.service.impl;

import mongo.MongoDemo.configuration.KafkaConfiguration;
import mongo.MongoDemo.dto.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MongoResourcesProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoResourcesProducer.class);

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Autowired
    public MongoResourcesProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(UserEvent userEvent) {
        LOGGER.info("#### Producing UserEvent: {}", userEvent);

        kafkaTemplate.send(KafkaConfiguration.KAFKA_TOPIC_USER, userEvent);

    }
}
