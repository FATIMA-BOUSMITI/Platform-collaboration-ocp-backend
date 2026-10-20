package com.ocp.organisation_service.ServiceImpl;

import com.ocp.organisation_service.client.AuthClient;
import com.ocp.organisation_service.dto.request.*;
import com.ocp.organisation_service.dto.response.AuthUserResponse;
import com.ocp.organisation_service.dto.response.AuthUserRoleAssignmentResponse;
import com.ocp.organisation_service.dto.response.DepartementResponseDTO;
import com.ocp.organisation_service.dto.response.RoleResponseDTO;
import com.ocp.organisation_service.dto.response.UserResponseDTO;
import com.ocp.organisation_service.entity.Departement;
import com.ocp.organisation_service.entity.UserProfile;
import com.ocp.organisation_service.mappers.UserMapper;
import com.ocp.organisation_service.repository.DepartementRepository;
import com.ocp.organisation_service.repository.TeamMemberRepository;
import com.ocp.organisation_service.repository.UserProfileRepository;
import com.ocp.organisation_service.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileServiceImpl.class);

	private final UserProfileRepository repository;
	private final DepartementRepository departementRepository;
	private final TeamMemberRepository teamMemberRepository;
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
		UserResponseDTO result=mapper.toResponse(saved);
 var createdRole=new RoleResponseDTO();createdRole.setId(request.getRoleId());result.setRoles(List.of(createdRole));
 return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponseDTO> getAllUsers() {
		long startedAt = System.nanoTime();
		List<UserProfile> profiles = repository.findAll();
		List<AuthUserRoleAssignmentResponse> assignments = authClient.getAllUserRoleAssignments();
		Map<UUID, RoleResponseDTO> rolesByUserId = assignments
			.stream()
			.collect(Collectors.toMap(
				AuthUserRoleAssignmentResponse::userId,
				this::toRoleResponse,
				(existingRole, duplicateRole) -> existingRole));

		List<UserResponseDTO> responses = profiles.stream().map(user -> {
			RoleResponseDTO role = user.getAuthUserId() == null
				? null
				: rolesByUserId.get(user.getAuthUserId());

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
				.roles(role == null ? List.of() : List.of(role))
				.build();
		}).toList();
		LOGGER.info(
			"Loaded {} user profiles and {} authentication role assignments in {} ms",
			profiles.size(),
			assignments.size(),
			(System.nanoTime() - startedAt) / 1_000_000);
		return responses;
	}

	private RoleResponseDTO toRoleResponse(AuthUserRoleAssignmentResponse assignment) {
		RoleResponseDTO role = new RoleResponseDTO();
		role.setId(assignment.roleId());
		role.setName(assignment.roleName());
		role.setDescription(assignment.roleDescription());
		return role;
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
	@Transactional(readOnly = true)
	public UserResponseDTO getUserByAuthUserId(UUID authUserId) {
		UserProfile user = repository.findByAuthUserId(authUserId)
			.orElseThrow(() -> new ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"User profile not found for authentication account"
			));

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

		if ((request.getRoleId() != null || request.getEmail() != null) && user.getAuthUserId() != null) {
			UpdateAuthUserRequest authRequest = new UpdateAuthUserRequest();
			authRequest.setRoleId(request.getRoleId());
 authRequest.setEmail(request.getEmail());
			authClient.updateUser(user.getAuthUserId(), authRequest);
		}

		UserResponseDTO response=mapper.toResponse(updatedUser);
 if(user.getAuthUserId()!=null) response.setRoles(authClient.getRolesByUserId(user.getAuthUserId()));
 return response;
	}

	@Override
	@Transactional
	public void deleteUser(UUID id) {
		UserProfile user = repository.findById(id)
			.orElseThrow(() -> new RuntimeException("User not found with id: " + id));

		LOGGER.info(
			"Deleting user profile id={} with linked authentication userId={}",
			user.getId(),
			user.getAuthUserId());
		teamMemberRepository.deleteByUserId(user.getId());

		if (user.getAuthUserId() != null) {
			authClient.deleteUserByUserId(user.getAuthUserId());
		}

		repository.delete(user);
	}
}
