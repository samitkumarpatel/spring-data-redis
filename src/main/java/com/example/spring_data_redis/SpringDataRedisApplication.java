package com.example.spring_data_redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

@SpringBootApplication
public class SpringDataRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataRedisApplication.class, args);
	}

	@Bean
	RedisTemplate<String, Coffee> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Coffee> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Coffee.class));
		return template;
	}

	@Bean
	RouterFunction<ServerResponse> routerFunction(RedisTemplate<String, Coffee> redisTemplate) {
		return RouterFunctions
				.route()
				.path("/coffee", builder -> builder
						.POST("", request -> {
							var coffee = request.body(Coffee.class);
							var newCoffee = new Coffee(UUID.randomUUID().toString(), coffee.name());
							redisTemplate.opsForValue().set(UUID.randomUUID().toString(), newCoffee);
							return ServerResponse.ok().build();
						})
						.GET("", request -> {
							//get all coffee
							var coffeeList = redisTemplate.opsForValue().multiGet(redisTemplate.keys("*"));
							return ServerResponse.ok().body(coffeeList);
						})
				)
				.build();
	}

}

record Coffee(String id, String name) { }