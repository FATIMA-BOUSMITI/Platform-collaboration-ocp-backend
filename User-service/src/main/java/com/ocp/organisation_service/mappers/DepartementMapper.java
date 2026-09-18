package com.ocp.organisation_service.mappers;
import com.ocp.organisation_service.dto.request.DepartementRequestDTO;
import com.ocp.organisation_service.dto.request.UserCreateRequestDTO;
import com.ocp.organisation_service.dto.response.UserResponseDTO;
import  com.ocp.organisation_service.entity.Departement;
import  com.ocp.organisation_service.dto.response.DepartementResponseDTO;
import com.ocp.organisation_service.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface DepartementMapper {
	 Departement toEntity(
		 DepartementRequestDTO dto
	);



	DepartementResponseDTO toResponse(
		Departement entity
	);
}
