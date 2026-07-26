package com.ocp.auth_service.entity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;

import java.time.LocalDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name ="Refresh_tokens")
@Getter
@Setter


public class RereshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)

	private UUID id;
	@Column(nullable = false , unique = true)
	private String token;
	@Column(nullable = false)
	private LocalDateTime expirayDate ;
	@Column(nullable = false )
	private boolean Revoked ;
	@Column(nullable = false)
	private LocalDateTime createdAt;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id" ,nullable = false)
	private UserCredential user ;



}
