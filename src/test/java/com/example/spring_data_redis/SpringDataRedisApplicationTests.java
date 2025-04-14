package com.example.spring_data_redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SpringDataRedisApplicationTests {

	@Test
	void contextLoads() {
	}

}
