package com.ocp.organisation_service.repository;

import com.ocp.organisation_service.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository
	extends JpaRepository<Team, UUID> {


	List<Team> findByDepartmentId(UUID departmentId);


}
