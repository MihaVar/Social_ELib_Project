package org.mvar.social_elib_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Properties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SocialELibProjectApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SocialELibProjectApplication.class);
		Properties properties = new Properties();
		properties.put("spring.data.mongodb.uri", System.getenv("MONGODB_URI"));
		application.setDefaultProperties(properties);

		application.run(args);
	}

}
