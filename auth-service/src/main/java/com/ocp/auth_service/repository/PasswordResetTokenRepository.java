package com.ocp.auth_service.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ocp.auth_service.entity.PasswordResetToken;
import com.ocp.auth_service.entity.UserCredential;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,UUID >{
	Optional<PasswordResetToken> findByToken(String token);
	void deleteByUserAndUsedFalse(UserCredential user);
	void deleteByUser(UserCredential user);

}
