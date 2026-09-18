package com.ocp.organisation_service.ServiceImpl;

import com.ocp.organisation_service.client.AuthClient;
import com.ocp.organisation_service.dto.request.*;
import com.ocp.organisation_service.dto.response.AuthUserResponse;
import com.ocp.organisation_service.dto.response.DepartementResponseDTO;
import com.ocp.organisation_service.dto.response.RoleResponseDTO;
import com.ocp.organisation_service.dto.response.UserResponseDTO;
import com.ocp.organisation_service.entity.Departement;
import com.ocp.organisation_service.entity.UserProfile;
import com.ocp.organisation_service.mappers.UserMapper;
import com.ocp.organisation_service.repository.DepartementRepository;
import com.ocp.organisation_service.repository.UserProfileRepository;
import com.ocp.organisation_service.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

	private final UserProfileRepository repository;
	private final DepartementRepository departementRepository;
	private final UserMapper mapper;
	private final AuthClient authClient;

	@Override
	@Transactional
	public UserResponseDTO createUser(UserCreateRequestDTO request) {
		if (repository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		Departement departement = departementRepository.findById(request.getDepartement())
			.orElseThrow(() -> new RuntimeException("Departement not found"));

		UUID profileId = UUID.randomUUID();
		CreateCredentialRequest credentialRequest = new CreateCredentialRequest();
		credentialRequest.setUserId(profileId);
		credentialRequest.setEmail(request.getEmail());
		credentialRequest.setRoleId(request.getRoleId());

		AuthUserResponse authUser = authClient.createUser(credentialRequest);
		if (authUser == null || authUser.getUserId() == null) {
			throw new RuntimeException("Auth service did not create the credential");
		}

		UserProfile user = mapper.toEntity(request);
		user.setId(profileId);
		user.setAuthUserId(authUser.getUserId());
		user.setDepartement(departement);

		UserProfile saved = repository.save(user);
		return mapper.toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponseDTO> getAllUsers() {
		return repository.findAll().stream().map(user -> {
			List<RoleResponseDTO> roles = List.of();
			try {
				if (user.getAuthUserId() != null) {
					roles = authClient.getRolesByUserId(user.getAuthUserId());
				}
			} catch (Exception ignored) {
				System.out.println("Pas de rôle pour : " + user.getEmail());
			}

			return UserResponseDTO.builder()
				.id(user.getId())
				.userId(user.getAuthUserId())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.phone(user.getPhone())
				.photoUrl(user.getPhotoUrl())
				.position(user.getPosition())
				.managerId(user.getManagerId())
				.departement(user.getDepartement() != null ? DepartementResponseDTO.builder()
					.id(user.getDepartement().getId())
					.name(user.getDepartement().getName())
					.build() : null)
				.roles(roles)
				.build();
		}).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponseDTO getUserById(UUID id) {
		UserProfile user = repository.findById(id)
			.orElseThrow(() -> new RuntimeException("User not found with id: " + id));

		List<RoleResponseDTO> roles = List.of();
		try {
			if (user.getAuthUserId() != null) {
				roles = authClient.getRolesByUserId(user.getAuthUserId());
			}
		} catch (Exception ignored) {
			System.out.println("Pas de rôle pour : " + user.getEmail());
		}

		UserResponseDTO response = mapper.toResponse(user);
		response.setRoles(roles);
		return response;
	}

	@Override
	@Transactional
	public UserResponseDTO updateUser(UUID id, UserUpdateRequestDTO request) {
		UserProfile user = repository.findById(id)
			.orElseThrow(() -> new RuntimeException("User not found with id: " + id));

		if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
		if (request.getLastName() != null) user.setLastName(request.getLastName());
		if (request.getEmail() != null) user.setEmail(request.getEmail());
		if (request.getPhone() != null) user.setPhone(request.getPhone());
		if (request.getPhotoUrl() != null) user.setPhotoUrl(request.getPhotoUrl());
		if (request.getPosition() != null) user.setPosition(request.getPosition());
		if (request.getManagerId() != null) user.setManagerId(request.getManagerId());
		if (request.getLanguage() != null) user.setLanguage(request.getLanguage());
		if (request.getTimezone() != null) user.setTimezone(request.getTimezone());

		if (request.getDepartement() != null) {
			Departement departement = departementRepository.findById(request.getDepartement())
				.orElseThrow(() -> new RuntimeException("Departement not found"));
			user.setDepartement(departement);
		}

		UserProfile updatedUser = repository.save(user);

		if (request.getRoleId() != null && user.getAuthUserId() != null) {
			UpdateAuthUserRequest authRequest = new UpdateAuthUserRequest();
			authRequest.setRoleId(request.getRoleId());
			authClient.updateUser(user.getAuthUserId(), authRequest);
		}

		return mapper.toResponse(updatedUser);
	}

	@Override
	@Transactional
	public void deleteUser(UUID id) {
		UserProfile user = repository.findById(id)
			.orElseThrow(() -> new RuntimeException("User not found with id: " + id));

		if (user.getAuthUserId() != null) {
			authClient.deleteUser(user.getAuthUserId());
		}

		repository.delete(user);
	}
}
