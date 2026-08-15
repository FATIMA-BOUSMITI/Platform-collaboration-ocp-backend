package com.ocp.project_management_service.mapper;

import com.ocp.project_management_service.dto.request.SubTaskRequestDTO;
import com.ocp.project_management_service.dto.response.SubTaskDTO;
import com.ocp.project_management_service.entity.SubTask;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubTaskMapper {
    SubTask toEntity(SubTaskRequestDTO request);
    SubTaskDTO toResponseDTO(SubTask Task);
}
