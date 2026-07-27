package com.ocp.auth_service.dto.request;



import lombok.Data;

@Data
public class UpdatePermissionRequest {

	private String name;

	private String description;

}
