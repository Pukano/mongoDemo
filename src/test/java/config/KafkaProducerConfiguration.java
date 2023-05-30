package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;
@Configuration
public class KafkaProducerConfiguration {

    @Bean
    public DefaultKafkaProducerFactory<?, ?> kafkaProducerFactory(KafkaProperties properties, ObjectMapper objectMapper) {
        final Map<String, Object> producerProperties = properties.buildProducerProperties();
        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>(objectMapper);
        jsonSerializer.configure(producerProperties, false);

        return new DefaultKafkaProducerFactory<>(producerProperties,
                new StringSerializer(),
                jsonSerializer);
    }
}
