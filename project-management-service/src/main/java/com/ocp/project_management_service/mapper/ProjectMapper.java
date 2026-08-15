package com.ocp.project_management_service.mapper;

import com.ocp.project_management_service.dto.request.ProjectRequestDTO;
import com.ocp.project_management_service.dto.response.ProjectResponseDTO;
import com.ocp.project_management_service.entity.Project;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toEntity(ProjectRequestDTO request);
    ProjectResponseDTO toResponseDTO(Project project);

}
