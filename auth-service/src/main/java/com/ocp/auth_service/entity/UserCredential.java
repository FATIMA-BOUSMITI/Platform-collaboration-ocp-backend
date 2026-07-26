package com.ocp.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Entity
@Table(name = "user_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EnableJpaAuditing
public class UserCredential {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true)
	private UUID userId;

	@Column(nullable = false, unique = true, length = 150)
	private String email;

	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	private Boolean enabled = true;

	@Column(nullable = false)
	private Boolean accountLocked = false;

	@Column(nullable = false)
	private Integer failedAttempts = 0;

	private LocalDateTime lastLogin;

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;
	@ManyToMany
	@JoinTable(
		name = "user_roles",
		joinColumns = @JoinColumn(name = "user_credential_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles = new HashSet<>();
	@OneToMany(mappedBy = "user")



	private List<LoginHistory> loginHistories = new ArrayList<>();
	@OneToMany(mappedBy = "user")

	private List<Session> session = new ArrayList<>();
	@OneToMany(mappedBy = "user")
	private List<RefreshToken> refeshTokens  =new ArrayList<>();
}
