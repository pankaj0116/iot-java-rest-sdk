package com.vois.iot.java.rest.sdk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class IotJavaRestSdkApplicationTests {

	@Test
	void contextLoads() {
		// Verify that the application context loads without throwing any exceptions
		assertDoesNotThrow(() -> IotJavaRestSdkApplication.main(new String[] {}));
	}

}
