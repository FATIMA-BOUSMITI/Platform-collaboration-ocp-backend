package com.ocp.project_management_service.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusDTO {

    @NotBlank
    private String status;
}
