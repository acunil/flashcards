package com.example.flashcards_backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FlashcardsBackendApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Test
	@Disabled("This does not need to run every time all tests are run, as it is resource intensive.")
	void contextLoads() {
		assertThat(context).isNotNull();
	}
}