package com.ocp.auth_service.mappers;

import com.ocp.auth_service.dto.response.SessionResponse;
import com.ocp.auth_service.entity.Session;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface SessionMapper {

	SessionResponse toResponse(Session session);

	List<SessionResponse> toResponse(List<Session> sessions);

	Set<SessionResponse> toResponse(Set<Session> sessions);

}
