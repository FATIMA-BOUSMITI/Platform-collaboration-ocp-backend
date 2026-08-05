package com.ocp.auth_service.mappers;

import com.ocp.auth_service.dto.response.UserResponse;
import com.ocp.auth_service.entity.UserCredential;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Mapper(
	componentModel = "spring",
	uses = RoleMapper.class
)
@Component
public interface UserCredentialMapper {

	UserResponse toResponse(UserCredential userCredential);

	List<UserResponse> toResponse(List<UserCredential> users);

	Set<UserResponse> toResponse(Set<UserCredential> users);

}
