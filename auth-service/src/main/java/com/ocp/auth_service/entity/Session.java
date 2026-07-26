package com.ocp.auth_service.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {


	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;


	private String device;


	private String ipAddress;


	private String userAgent;


	private LocalDateTime loginTime;


	private LocalDateTime lastActivity;


	private Boolean active = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserCredential user;

}
