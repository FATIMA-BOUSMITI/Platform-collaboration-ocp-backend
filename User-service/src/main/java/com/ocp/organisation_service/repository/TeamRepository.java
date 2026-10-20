package com.ocp.organisation_service.repository;

import com.ocp.organisation_service.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository
	extends JpaRepository<Team, UUID> {


	@org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @org.springframework.data.jpa.repository.Query("select t from Team t where t.id = :id")
 java.util.Optional<Team> lockById(@org.springframework.data.repository.query.Param("id") UUID id);
 List<Team> findByDepartmentId(UUID departmentId);


}
