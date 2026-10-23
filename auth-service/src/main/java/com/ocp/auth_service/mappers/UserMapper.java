package com.ocp.auth_service.mappers;

import com.ocp.auth_service.dto.response.UserResponse;
import com.ocp.auth_service.entity.UserCredential;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

	public UserResponse toResponse(UserCredential user) {

		String roleName = user.getRole() != null
			? user.getRole().getName()
			: null;

		return new UserResponse(
			user.getUserId(),
			user.getEmail(),
			user.getEnabled(),
			user.getAccountLocked(),
			user.getLastLogin(),
			user.getCreatedAt(),
			roleName
		);
	}
}
