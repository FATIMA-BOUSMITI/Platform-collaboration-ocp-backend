package com.ocp.auth_service.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="permissions")
public class Permission {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column
	private String name ;
    @Column
	private String description;
}
