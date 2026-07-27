package com.ocp.auth_service.mappers;


import com.ocp.auth_service.dto.response.RoleResponse;

import com.ocp.auth_service.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring" ,uses = PermissionMapper.class)
public interface RoleMapper {

	RoleResponse toResponse(
		Role role
	);
	List<RoleResponse> toResponse(List<Role> role );
//	Set<RoleResponse> toResponse(Set<Role> role);

}
