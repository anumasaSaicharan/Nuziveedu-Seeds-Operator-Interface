package com.nsl.operatorInterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAutoConfiguration
@ComponentScan("com.nsl.operatorInterface")
@SpringBootApplication
public class NuziveeduSeedsOperatorInterfaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NuziveeduSeedsOperatorInterfaceApplication.class, args);
	}

}
