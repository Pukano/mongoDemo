package config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

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
