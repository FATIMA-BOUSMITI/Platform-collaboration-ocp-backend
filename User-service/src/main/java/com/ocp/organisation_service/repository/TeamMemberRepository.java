package com.ocp.organisation_service.repository;

import com.ocp.organisation_service.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamMemberRepository
	extends JpaRepository<TeamMember, UUID> {


	List<TeamMember> findByUserId(UUID userId);


}
