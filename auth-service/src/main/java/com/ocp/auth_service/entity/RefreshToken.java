package com.ocp.auth_service.entity;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import java.util.UUID;

@Entity
@Table(name ="Refresh_tokens")
@Getter
@Setter


public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)

	private UUID id;
	@Column(nullable = false, unique = true, length = 500)
	private String refreshToken;
	@Column(nullable = false)
	private LocalDateTime expiryDate ;
	@Column(nullable = false )
	private boolean revoked ;
	@Column(nullable = false)
	private LocalDateTime createdAt;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id" ,nullable = false)
	private UserCredential user ;



}
