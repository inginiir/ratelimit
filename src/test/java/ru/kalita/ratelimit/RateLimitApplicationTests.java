package ru.kalita.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kalita.ratelimit.controllers.ApiController;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RateLimitApplicationTests {

	private final ApiController apiController;

	@Autowired
	RateLimitApplicationTests(ApiController apiController) {
		this.apiController = apiController;
	}

	@Test
	void contextLoads() {
		assertNotNull(apiController);
	}
}
