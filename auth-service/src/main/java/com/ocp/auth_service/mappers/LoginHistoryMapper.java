package com.ocp.auth_service.mappers;

import com.ocp.auth_service.dto.response.LoginHistoryResponse;
import com.ocp.auth_service.dto.response.PermissionResponse;
import com.ocp.auth_service.entity.LoginHistory;
import com.ocp.auth_service.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginHistoryMapper {

	LoginHistoryResponse toResponse(
		LoginHistory loginhistory
	);

}
