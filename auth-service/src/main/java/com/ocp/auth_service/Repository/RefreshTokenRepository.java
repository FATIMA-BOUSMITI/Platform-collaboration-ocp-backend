package com.ocp.auth_service.Repository;



import java.util.Optional;
import java.util.UUID;

import com.ocp.auth_service.entity.RefreshToken;
import com.ocp.auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface RefreshTokenRepository
	extends JpaRepository<RefreshToken, UUID> {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	List<RefreshToken> findByUser(UserCredential user);
	List<RefreshToken> findByUserAndRevokedFalse(UserCredential user);
	List<RefreshToken> findByRevoked(boolean revoked);

	List<RefreshToken> findByExpiryDateBefore(LocalDateTime date);

	void deleteByUser(UserCredential user);
	List<RefreshToken> findAllByUser_Id(UUID credentialId);

	void deleteByExpiryDateBefore(LocalDateTime date);


}
