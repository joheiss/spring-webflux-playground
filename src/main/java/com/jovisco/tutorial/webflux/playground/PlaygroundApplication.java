package com.jovisco.tutorial.webflux.playground;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.web.reactive.config.EnableWebFlux;

@Slf4j
// @EnableWebFlux
@SpringBootApplication()
@EnableR2dbcRepositories(basePackages = "com.jovisco.tutorial.webflux.playground")
public class PlaygroundApplication {

	@Bean
	ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("sql/schema.sql")));

		return initializer;
	}

	public static void main(String[] args) {
		log.info("Application started");
		SpringApplication.run(PlaygroundApplication.class, args);
	}

}
