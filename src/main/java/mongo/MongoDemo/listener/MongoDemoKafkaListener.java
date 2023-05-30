package mongo.MongoDemo.listener;

import mongo.MongoDemo.configuration.KafkaConfiguration;
import mongo.MongoDemo.dto.UserEvent;
import mongo.MongoDemo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Controller;

@Controller
public class MongoDemoKafkaListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDemoKafkaListener.class);
    private static final String LISTENER_ID = "mesScanChangeListener";
    private static final String GROUP_ID = "groupIdUserListener";

    private final UserService userService;

    @Autowired
    public MongoDemoKafkaListener(UserService userService) {
       
        this.userService = userService;
    }

    @KafkaListener(id = LISTENER_ID,
            topics = KafkaConfiguration.KAFKA_TOPIC_USER,
            groupId = GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory")

    public void consumeLeaGate(UserEvent userEvent) {
        switch (userEvent.eventType()){
            case DELETE_USER -> userService.deleteUser(userEvent.id());
            case CREATE_USER -> userService.saveUser(userEvent.userRequest());
        }

        LOGGER.info("userEvent processed");
    }
}
