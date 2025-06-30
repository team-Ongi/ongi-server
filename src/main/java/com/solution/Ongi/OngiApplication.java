package com.solution.Ongi;

import com.solution.Ongi.infra.firebase.FirebaseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties(FirebaseProperties.class)
public class OngiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OngiApplication.class, args);
	}

}
