package com.ocp.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, length = 50)
	private String name;

	@Column(length = 255)
	private String description;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "role_permissions",
		joinColumns = @JoinColumn(name = "role_id"),
		inverseJoinColumns = @JoinColumn(name = "permission_id")
	)
	@Builder.Default
	private Set<Permission> permissions = new HashSet<>();

	@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
	@Builder.Default
	private Set<UserCredential> users = new HashSet<>();

}
