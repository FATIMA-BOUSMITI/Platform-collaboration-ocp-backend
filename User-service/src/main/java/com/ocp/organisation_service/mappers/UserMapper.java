package com.ocp.organisation_service.mappers;


import com.ocp.organisation_service.dto.request.CreateUserRequest;
import com.ocp.organisation_service.dto.request.UpdateUserRequest;
import com.ocp.organisation_service.dto.response.UserResponse;
import com.ocp.organisation_service.entity.User;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {


	// DTO -> Entity
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "authUserId", ignore = true)
	@Mapping(target = "department", ignore = true)
	User toEntity(CreateUserRequest request);



	// Entity -> Response DTO
	@Mapping(
		target = "departmentId",
		source = "department.id"
	)
	UserResponse toResponse(User user);



	// Update Entity
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "email", ignore = true)
	@Mapping(target = "authUserId", ignore = true)
	@Mapping(target = "department", ignore = true)
	void updateEntity(
		UpdateUserRequest request,
		@MappingTarget User user
	);

}
