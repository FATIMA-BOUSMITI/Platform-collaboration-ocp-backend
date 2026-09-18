package com.ocp.organisation_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class RestClientConfig {


	@Bean
	public RestClient restClient(
		@Value("${auth.service.url}") String authUrl
	){

		return RestClient.builder()
			.baseUrl(authUrl)
			.build();
	}

}
