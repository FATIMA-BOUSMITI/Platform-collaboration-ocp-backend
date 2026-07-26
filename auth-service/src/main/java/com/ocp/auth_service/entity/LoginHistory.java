package com.ocp.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "login_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private LocalDateTime loginDate;

	@Column(nullable = false)
	private String ipAddress;

	@Column(nullable = false)
	private String device;

	@Column(nullable = false)
	private Boolean success;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_credential_id", nullable = false)
	private UserCredential user;
}
