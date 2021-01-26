package com.wei.productivity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.wei.productivity")
public class ProductivityApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductivityApplication.class, args);
	}

}
