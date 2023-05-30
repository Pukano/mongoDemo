package mongo.MongoDemo.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
@Import({KafkaConsumerConfiguration.class})
public class KafkaConfiguration {
    public static final String KAFKA_TOPIC_USER = "user-kafka-topic";

    @Bean
    public NewTopic createNewUserTopic() {
        return TopicBuilder.name(KAFKA_TOPIC_USER)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
