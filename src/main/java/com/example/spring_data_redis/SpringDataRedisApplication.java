package com.example.spring_data_redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.UUID;

@SpringBootApplication
public class SpringDataRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataRedisApplication.class, args);
	}

	@Bean
	ReactiveRedisOperations<String, Coffee> redisOperations(ReactiveRedisConnectionFactory factory) {
		Jackson2JsonRedisSerializer<Coffee> serializer = new Jackson2JsonRedisSerializer<>(Coffee.class);

		RedisSerializationContext.RedisSerializationContextBuilder<String, Coffee> builder =
				RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

		RedisSerializationContext<String, Coffee> context = builder.value(serializer).build();

		return new ReactiveRedisTemplate<>(factory, context);
	}

	@Bean
	RouterFunction<ServerResponse> routerFunction(ReactiveRedisOperations<String, Coffee> redisOperations) {
		return RouterFunctions
				.route()
				.path("/coffee", builder -> builder
						.POST("", request -> {
							return request.bodyToMono(Coffee.class)
									.map(coffee -> new Coffee(UUID.randomUUID().toString(), coffee.name()))
										.flatMap(coffee -> redisOperations.opsForValue().set(coffee.id(), coffee))
									.then(ServerResponse.ok().build());
						})
						.GET("", request -> {
							return redisOperations.keys("*")
									.flatMap(redisOperations.opsForValue()::get)
									.collectList()
									.flatMap(coffees -> ServerResponse.ok().bodyValue(coffees));
						})
				)
				.build();
	}

}

record Coffee(String id, String name) { }