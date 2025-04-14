package com.example.spring_data_redis;

import org.springframework.boot.SpringApplication;

public class TestSpringDataRedisApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringDataRedisApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
