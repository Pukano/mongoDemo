package mongo.MongoDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "src/test/java/kafka")
public class MongoDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongoDemoApplication.class, args);
	}

}
