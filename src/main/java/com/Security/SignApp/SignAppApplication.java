package com.Security.SignApp;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class SignAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignAppApplication.class, args);
		log.info("SIGN-APP HAS STARTED...");
	}

}
