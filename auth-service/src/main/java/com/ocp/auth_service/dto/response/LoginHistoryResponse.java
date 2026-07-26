package com.ocp.auth_service.dto.response;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
public class LoginHistoryResponse {


	private UUID id;

	private LocalDateTime loginDate;

	private String ipAddress;

	private String device;

	private Boolean success;
}
