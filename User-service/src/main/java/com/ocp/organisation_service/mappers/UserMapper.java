package com.ocp.organisation_service.mappers;

import com.ocp.organisation_service.dto.request.UserCreateRequestDTO;
import com.ocp.organisation_service.dto.response.UserResponseDTO;
import com.ocp.organisation_service.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "departement", ignore = true)
	UserProfile toEntity(UserCreateRequestDTO request);

	@Mapping(source = "authUserId", target = "userId")
	@Mapping(target = "roles", ignore = true)
	UserResponseDTO toResponse(UserProfile entity);
}
