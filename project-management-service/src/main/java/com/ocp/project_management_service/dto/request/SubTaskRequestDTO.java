package com.ocp.project_management_service.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubTaskRequestDTO {

    @NotBlank
    private String title ;

}
