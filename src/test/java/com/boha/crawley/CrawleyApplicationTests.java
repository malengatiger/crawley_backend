package com.boha.crawley;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrawleyApplicationTests {

	@Test
	void dataControllerIsNotNull() {
		WhoIsService dataController = new WhoIsService();
		Assertions.assertNotNull(dataController);
	}

}
