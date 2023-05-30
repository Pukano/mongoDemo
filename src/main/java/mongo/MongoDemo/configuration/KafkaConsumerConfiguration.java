package mongo.MongoDemo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import mongo.MongoDemo._PackageInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;
@Configuration
public class KafkaConsumerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfiguration.class);

    private static final String DEAD_LETTER_POSTFIX = ".DLT";

    @Bean
    public DefaultKafkaConsumerFactory<?, ?> kafkaConsumerFactory(KafkaProperties properties, ObjectMapper objectMapper) {
        final Map<String, Object> consumerProperties = properties.buildConsumerProperties();
        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>(objectMapper);
        jsonDeserializer.configure(consumerProperties, false);
        jsonDeserializer.addTrustedPackages(_PackageInfo.class.getPackageName() + ".*");

        final ErrorHandlingDeserializer<Object> objectErrorHandlingDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(consumerProperties,
                new StringDeserializer(),
                objectErrorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
                                                                                       ConsumerFactory<Object, Object> kafkaConsumerFactory,
                                                                                       KafkaProperties kafkaProperties) {
        final ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);

        DefaultKafkaProducerFactory<String, byte[]> defaultKafkaProducerFactory =
                new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties(), new StringSerializer(), new ByteArraySerializer());
        KafkaTemplate<String, byte[]> bytesKafkaTemplate = new KafkaTemplate<>(defaultKafkaProducerFactory);

        factory.setCommonErrorHandler(
                new DefaultErrorHandler(
                        new DeadLetterPublishingRecoverer(bytesKafkaTemplate,
                                (rec, ex) -> {
                                    LOGGER.error("Error in process (deserialization) kafka message. Exception: {},  record: {}", ex, rec.value());
                                    return new TopicPartition(rec.topic() + DEAD_LETTER_POSTFIX, rec.partition());
                                    // 4 seconds pause, 3 retries.
                                }), new FixedBackOff(0L, 0L))
        );

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}
