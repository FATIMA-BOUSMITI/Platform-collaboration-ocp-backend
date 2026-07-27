package com.ocp.auth_service.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, length = 500)
	private String token;

	@ManyToOne
	@JoinColumn(name = "user_credential_id")
	private UserCredential user;

	private LocalDateTime expiryDate;

	private Boolean used;

	private LocalDateTime createdAt;
}
