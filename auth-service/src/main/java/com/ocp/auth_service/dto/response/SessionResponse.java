package com.ocp.auth_service.dto.response;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.*;
import java.util.UUID;
public class SessionResponse {


	private UUID id;

	private String device;

	private String ipAddress;

	private String userAgent;

	private LocalDateTime loginTime;

	private LocalDateTime lastActivity;

	private Boolean active;
}
