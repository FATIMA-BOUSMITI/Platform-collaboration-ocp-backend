package com.ocp.organisation_service.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthClient {

	private final RestClient restClient;

	public AuthUserResponse getUser(UUID id) {

		return restClient.get()
			.uri("/api/auth/users/{id}", id)
			.retrieve()
			.body(AuthUserResponse.class);
	}
}
