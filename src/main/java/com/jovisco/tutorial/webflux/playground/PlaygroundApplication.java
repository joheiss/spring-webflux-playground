package com.jovisco.tutorial.webflux.playground;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class PlaygroundApplication {

	public static void main(String[] args) {
		log.info("Application started");
		SpringApplication.run(PlaygroundApplication.class, args);
	}

}
