package com.ocp.organisation_service.repository;

import com.ocp.organisation_service.entity.Departement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;



@Repository
public interface DepartementRepository
	extends JpaRepository<Departement, UUID> {




}
