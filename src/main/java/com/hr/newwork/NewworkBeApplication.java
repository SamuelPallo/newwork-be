package com.hr.newwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class NewworkBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewworkBeApplication.class, args);
	}

}
