package com.ocp.organisation_service.repository;

import com.ocp.organisation_service.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository
	extends JpaRepository<Department, UUID> {


	Optional<Department> findByName(String name);


	boolean existsByName(String name);

}
