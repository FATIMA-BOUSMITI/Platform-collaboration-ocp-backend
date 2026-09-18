package com.ocp.organisation_service.services;

import com.ocp.organisation_service.dto.request.UserCreateRequestDTO;
import com.ocp.organisation_service.dto.request.UserUpdateRequestDTO;
import com.ocp.organisation_service.dto.response.UserResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserProfileService {

	UserResponseDTO createUser(UserCreateRequestDTO request);

	List<UserResponseDTO> getAllUsers();

	UserResponseDTO getUserById(UUID id);

	UserResponseDTO updateUser(UUID id, UserUpdateRequestDTO request);

	void deleteUser(UUID id);
}
