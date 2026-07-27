package com.ocp.auth_service.Repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ocp.auth_service.entity.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,UUID >{
	Optional<PasswordResetToken> findByToken(String token);


}
