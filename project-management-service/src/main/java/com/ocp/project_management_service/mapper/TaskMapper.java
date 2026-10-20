package com.ocp.project_management_service.mapper;
import com.ocp.project_management_service.dto.request.TaskRequestDTO;
import com.ocp.project_management_service.dto.response.TaskResponseDTO;
import com.ocp.project_management_service.entity.Task;
import org.mapstruct.*;
@Mapper(componentModel="spring")
public interface TaskMapper {
 Task toEntity(TaskRequestDTO request);
 @Mapping(target="projectId",source="project.id")
 @Mapping(target="position",source="sortIndex")
 @Mapping(target="estimatedTimeMinutes",expression="java(task.getEstimatedTime()==null ? 0L : task.getEstimatedTime().toMinutes())")
 @Mapping(target="realTimeMinutes",expression="java(task.getRealTime()==null ? 0L : task.getRealTime().toMinutes())")
 @Mapping(target="comments",source="taskComments")
 TaskResponseDTO toResponseDTO(Task task);
}
