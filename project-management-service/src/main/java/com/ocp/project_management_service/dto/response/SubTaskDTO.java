package com.ocp.project_management_service.dto.response;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SubTaskDTO {

    private UUID id ;
    private String title ;
    private boolean isDone;
}
