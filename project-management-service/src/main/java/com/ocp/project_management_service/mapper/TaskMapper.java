package com.ocp.project_management_service.mapper;

import com.ocp.project_management_service.dto.request.TaskRequestDTO;
import com.ocp.project_management_service.dto.response.TaskResponseDTO;
import com.ocp.project_management_service.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(TaskRequestDTO request);
    TaskResponseDTO toResponseDTO(Task Task);

}
