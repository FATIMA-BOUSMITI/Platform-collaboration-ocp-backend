package com.ocp.organisation_service.client;


import com.ocp.organisation_service.dto.request.AssignRoleRequest;
import com.ocp.organisation_service.dto.request.CreateCredentialRequest;
import com.ocp.organisation_service.dto.request.UpdateAuthUserRequest;
import com.ocp.organisation_service.dto.response.AuthUserResponse;
import com.ocp.organisation_service.dto.response.RoleResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;



import com.ocp.organisation_service.dto.request.CreateCredentialRequest;
import com.ocp.organisation_service.dto.response.AuthUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthClient {

	private final RestClient restClient;

	public AuthUserResponse createUser(CreateCredentialRequest request) {
		return restClient
			.post()
			.uri("/api/auth/users")
			.body(request)
			.retrieve()
			.body(AuthUserResponse.class);
	}

	public List<RoleResponseDTO> getRolesByUserId(UUID userId) {
		RoleResponseDTO role = restClient.get()
			.uri("/api/auth/users/{userId}/roles", userId)
			.retrieve()
			.body(RoleResponseDTO.class);

		return role == null ? List.of() : List.of(role);
	}

	public AuthUserResponse assignRole(AssignRoleRequest request) {
		return restClient
			.post()
			.uri("/api/auth/users/assign-roles")
			.body(request)
			.retrieve()
			.body(AuthUserResponse.class);
	}

	public AuthUserResponse updateUser(UUID id, UpdateAuthUserRequest request) {
		return restClient
			.put()
			.uri("/api/auth/users/{id}", id)
			.body(request)
			.retrieve()
			.body(AuthUserResponse.class);
	}

	public void deleteUser(UUID id) {
		restClient.delete()
			.uri("/api/auth/users/{id}", id)
			.retrieve()
			.toBodilessEntity();
	}
}



