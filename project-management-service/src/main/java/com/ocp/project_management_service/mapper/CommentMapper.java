package com.ocp.project_management_service.mapper;

import com.ocp.project_management_service.dto.request.CommentRequestDTO;
import com.ocp.project_management_service.dto.response.CommentDTO;
import com.ocp.project_management_service.entity.SubTask;
import com.ocp.project_management_service.entity.TaskComment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    TaskComment toEntity(CommentRequestDTO request);
    CommentDTO toResponse(TaskComment comment);

}
