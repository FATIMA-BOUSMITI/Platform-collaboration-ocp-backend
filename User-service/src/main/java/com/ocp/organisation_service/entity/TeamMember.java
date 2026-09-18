package com.ocp.organisation_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="team_members")
@Data
public class TeamMember {


	@Id
	@GeneratedValue
	private UUID id;


	private UUID teamId;


	private UUID userId;


	private String roleInTeam;


	private LocalDateTime joinedAt;


}
