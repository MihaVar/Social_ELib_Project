package org.mvar.social_elib_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableMongoRepositories
public class SocialELibProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialELibProjectApplication.class, args);
	}

}
