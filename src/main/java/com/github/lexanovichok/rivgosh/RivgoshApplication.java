package com.github.lexanovichok.rivgosh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.github.lexanovichok.rivgosh.model")
public class RivgoshApplication {

	public static void main(String[] args) {
		SpringApplication.run(RivgoshApplication.class, args);
	}

}
