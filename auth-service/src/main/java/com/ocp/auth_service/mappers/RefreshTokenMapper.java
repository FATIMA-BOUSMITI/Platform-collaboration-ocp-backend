package com.ocp.auth_service.mappers;


import com.ocp.auth_service.dto.response.RefreshTokenResponse;

import com.ocp.auth_service.entity.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface RefreshTokenMapper {
	@Mapping(target = "accessToken", ignore = true)
	@Mapping(target = "expiryDate", ignore = true)

	RefreshTokenResponse toResponse(
		RefreshToken refreshtoken
	);

}

