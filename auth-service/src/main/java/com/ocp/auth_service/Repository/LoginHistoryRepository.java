package com.ocp.auth_service.repository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import com.ocp.auth_service.entity.LoginHistory;
import com.ocp.auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginHistoryRepository
	extends JpaRepository<LoginHistory, UUID> {

	Optional<LoginHistory> findByUser(UserCredential user);

	List<LoginHistory> findByUserOrderByLoginDateDesc(UserCredential user);

	List<LoginHistory> findBySuccess(boolean success);
}
