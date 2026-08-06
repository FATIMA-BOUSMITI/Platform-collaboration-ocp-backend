package com.ocp.auth_service.services;

import com.ocp.auth_service.repository.RefreshTokenRepository;
import com.ocp.auth_service.repository.UserCredentialRepository;
import com.ocp.auth_service.dto.response.AuthResponse;
import com.ocp.auth_service.dto.response.RefreshTokenResponse;
import com.ocp.auth_service.entity.RefreshToken;
import com.ocp.auth_service.entity.UserCredential;
import java.time.LocalDateTime;

import com.ocp.auth_service.mappers.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtService jwtService;


	private final  RefreshTokenMapper refreshTokenMapper;
	private final  UserCredentialRepository userCredentialRepository;

	public RefreshTokenResponse createRefreshToken(UserCredential user) {
		RefreshToken refreshToken = new RefreshToken();
		System.out.println(refreshToken);
		String jwtToken = jwtService.generateRefreshToken(user);
		refreshToken.setRefreshToken(jwtToken);

		// generate token //la fonction generate refreshtoken fun
		refreshToken.setUser(user);
		refreshToken.setCreatedAt(LocalDateTime.now());
		// ajoute la date de cration
		refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
		refreshToken.setRevoked(false);
		RefreshToken saved= refreshTokenRepository.save(refreshToken);

		System.out.println(" token"+saved.getRefreshToken());
		RefreshTokenResponse response = refreshTokenMapper.toResponse(saved);
		System.out.println(response);

		return refreshTokenMapper.toResponse(saved);
	}

	public RefreshTokenResponse refreshToken(String token) {
		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token).orElseThrow(()-> new RuntimeException("Le token n'existe pas "));
		if (!"REFRESH".equals(jwtService.extractTokenType(token))) {
			throw new RuntimeException("Ce token n'est pas un Refresh Token");
		}
		if(refreshToken.isRevoked()){
			throw new RuntimeException("Deja expiré");
		}
		if(jwtService.isTokenExpired(token)){
			throw new RuntimeException("Revoked");
		}
		UserCredential user =refreshToken.getUser();
		String acessToken = jwtService.generateAccessToken(user);




		return RefreshTokenResponse.builder()
			.accessToken(acessToken)
			.refreshToken(token)
			.expiryDate(jwtService.extractExpiration(token))
			.build();

	}

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByRefreshToken(token);
	}

	public RefreshToken verifyExpiration(String token) {

		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
			.orElseThrow(() -> new RuntimeException("Refresh token introuvable"));

		if (refreshToken.isRevoked()) {
			throw new RuntimeException("Le refresh token est révoqué");
		}

		if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Le refresh token a expiré");
		}

		return refreshToken;
	}

	public void revokeToken(String token) {

		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
			.orElseThrow(() -> new RuntimeException("Refresh token introuvable"));

		refreshToken.setRevoked(true);

		refreshTokenRepository.save(refreshToken);
	}

	public void revokeAllTokens(UUID credentialId) {

		List<RefreshToken> tokens =
			refreshTokenRepository.findAllByUser_Id(credentialId);

		for (RefreshToken token : tokens) {
			token.setRevoked(true);
		}

		refreshTokenRepository.saveAll(tokens);
	}

	public boolean isValid(String token) {

		Optional<RefreshToken> optional =
			refreshTokenRepository.findByRefreshToken(token);

		if (optional.isEmpty()) {
			return false;
		}

		RefreshToken refreshToken = optional.get();

		return !refreshToken.isRevoked()
			&& refreshToken.getExpiryDate().isAfter(LocalDateTime.now());
	}
	@Scheduled(cron = "0 0 2 * * *")

	public void deleteExpiredTokens() {

		refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());

	}
}
