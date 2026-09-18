package com.ocp.auth_service.services;

import com.ocp.auth_service.Repository.LoginHistoryRepository;
import com.ocp.auth_service.dto.request.AssignRoleRequest;
import com.ocp.auth_service.dto.request.CreateUserRequest;
import com.ocp.auth_service.dto.request.UpdateUserRequest;
import com.ocp.auth_service.dto.response.UserResponse;
import com.ocp.auth_service.dto.response.UserStatsResponse;
import com.ocp.auth_service.entity.Role;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.exception.EmailAlreadyExistsException;
import com.ocp.auth_service.exception.UserNotFoundException;
import com.ocp.auth_service.mappers.UserMapper;
import com.ocp.auth_service.repository.RoleRepository;
import com.ocp.auth_service.repository.UserCredentialRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

	private final UserCredentialRepository userCredentialRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final RoleRepository roleRepository;
	private final LoginHistoryRepository loginHistoryRepository;


	// =========================
	// CREATE USER
	// =========================

	@Transactional
	public UserResponse createUser(CreateUserRequest request) {

		if (userCredentialRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException(request.getEmail());
		}

		// Récupérer le rôle
		Role role = roleRepository.findById(request.getRoleId())
			.orElseThrow(() ->
				new RuntimeException("Role not found")
			);

		UserCredential user = UserCredential.builder()
			.userId(request.getUserId())
			.email(request.getEmail())
			.passwordHash(
				passwordEncoder.encode("test12789")
			)
			.enabled(false)
			.accountLocked(true)
			.failedAttempts(0)
			.role(role)
			.build();

		UserCredential savedUser =
			userCredentialRepository.save(user);

		return userMapper.toResponse(savedUser);
	}


	// =========================
	// GET USER BY ID
	// =========================

	@Transactional(readOnly = true)
	public UserResponse getUserById(UUID id) {

		UserCredential user =
			userCredentialRepository.findById(id)
				.orElseThrow(() ->
					new UserNotFoundException(id.toString())
				);

		return userMapper.toResponse(user);
	}


	// =========================
	// GET ALL USERS
	// =========================

	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {

		return userCredentialRepository.findAll()
			.stream()
			.map(userMapper::toResponse)
			.collect(Collectors.toList());
	}


	// =========================
	// UPDATE USER
	// =========================


	@Transactional
	public UserResponse updateUser(UUID userId, UpdateUserRequest request) {

		UserCredential user = userCredentialRepository.findByUserId(userId)
			.orElseThrow(() ->
				new UserNotFoundException(userId.toString())
			);

		if (request.getEmail() != null) {
			user.setEmail(request.getEmail());
		}

		if (request.getEnabled() != null) {
			user.setEnabled(request.getEnabled());
		}

		if (request.getAccountLocked() != null) {
			user.setAccountLocked(request.getAccountLocked());
		}

		if (request.getRoleId() != null) {
			Role role = roleRepository.findById(request.getRoleId())
				.orElseThrow(() -> new RuntimeException("Role not found"));

			user.setRole(role);
		}

		UserCredential updatedUser =
			userCredentialRepository.save(user);

		return userMapper.toResponse(updatedUser);
	}

	// =========================
	// ASSIGN ONE ROLE
	// =========================

	@Transactional
	public UserResponse assignRole(AssignRoleRequest request) {

		UserCredential user =
			userCredentialRepository.findById(request.getUserId())
				.orElseThrow(() ->
					new UserNotFoundException(
						request.getUserId().toString()
					)
				);

		// Récupérer le rôle
		Role role =
			roleRepository.findById(request.getRoleId())
				.orElseThrow(() ->
					new RuntimeException("Role not found")
				);

		// Un seul rôle par utilisateur
		user.setRole(role);

		UserCredential updatedUser =
			userCredentialRepository.save(user);

		return userMapper.toResponse(updatedUser);
	}


	// =========================
	// DELETE USER
	// =========================

	@Transactional
	public void deleteUser(UUID id) {

		if (!userCredentialRepository.existsById(id)) {
			throw new UserNotFoundException(id.toString());
		}

		userCredentialRepository.deleteById(id);
	}


	// =========================
	// USER STATS
	// =========================

	@Transactional(readOnly = true)
	public UserStatsResponse getUserStats() {

		long totalUsers =
			userCredentialRepository.count();

		long activeUsers =
			userCredentialRepository.countByEnabledTrue();

		long lockedAccounts =
			userCredentialRepository.countByAccountLockedTrue();

		long totalFailedAttempts =
			userCredentialRepository.sumFailedAttempts();

		long usersWithFailedAttempts =
			userCredentialRepository
				.countByFailedAttemptsGreaterThan(0);

		return new UserStatsResponse(
			totalUsers,
			activeUsers,
			lockedAccounts,
			totalFailedAttempts,
			usersWithFailedAttempts
		);
	}


	// =========================
	// FAILED ATTEMPTS 24H
	// =========================

	@Transactional(readOnly = true)
	public long getFailedAttemptsLast24Hours() {

		LocalDateTime end = LocalDateTime.now();
		LocalDateTime start =
			end.minusHours(24);

		return loginHistoryRepository
			.countBySuccessFalseAndLoginDateBetween(
				start,
				end
			);
	}
}
