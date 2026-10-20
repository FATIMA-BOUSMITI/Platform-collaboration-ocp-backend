package com.ocp.organisation_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

	@Bean
	public RestClient restClient(
		@Value("${auth.service.url}") String authUrl) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(Duration.ofSeconds(3));
		requestFactory.setReadTimeout(Duration.ofSeconds(20));

		return RestClient.builder()
			.baseUrl(authUrl)
			.requestFactory(requestFactory)
			.requestInterceptor((request, body, execution) -> {
				if (RequestContextHolder.getRequestAttributes()
					instanceof ServletRequestAttributes attributes) {
					String authorization = attributes.getRequest()
						.getHeader(HttpHeaders.AUTHORIZATION);
					if (authorization != null && !authorization.isBlank()) {
						request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorization);
					}
				}
				return execution.execute(request, body);
			})
			.build();
	}

}
