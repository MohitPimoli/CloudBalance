package com.cloudbalance.lens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class LensApplication {
	public static void main(String[] args) {
		SpringApplication.run(LensApplication.class, args);
	}
}
