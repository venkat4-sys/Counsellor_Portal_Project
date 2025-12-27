package com.infinity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:query.xml"})
public class CounsellorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CounsellorApplication.class, args);
	}

}
