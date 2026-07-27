package com.ocp.auth_service.mappers;

import com.ocp.auth_service.dto.response.PermissionResponse;
import com.ocp.auth_service.entity.Permission;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

	PermissionResponse toResponse(
		Permission permission
	);

	Set<PermissionResponse> toResponse(Set<Permission> permissions);


}
