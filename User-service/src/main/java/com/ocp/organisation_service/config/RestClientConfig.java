package com.ocp.organisation_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	@Bean
	RestClient restClient(RestClient.Builder builder) {
		return builder
			.baseUrl("http://localhost:8080")
			.build();
	}
}
