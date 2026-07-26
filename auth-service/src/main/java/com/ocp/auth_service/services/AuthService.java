package com.ocp.auth_service.services;

import com.ocp.auth_service.Repository.PasswordResetTokenRepository;
import com.ocp.auth_service.Repository.RefreshTokenRepository;
import com.ocp.auth_service.Repository.UserCredentialRepository;
import com.ocp.auth_service.dto.request.*;

import com.ocp.auth_service.dto.response.AuthResponse;
import com.ocp.auth_service.dto.response.RefreshTokenResponse;
import com.ocp.auth_service.entity.PasswordResetToken;
import com.ocp.auth_service.entity.RefreshToken;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.exception.AccountDisabledException;
import com.ocp.auth_service.exception.AccountLockedException;
import com.ocp.auth_service.exception.InvalidCredentialsException;
import com.ocp.auth_service.exception.UserNotFoundException;
import com.ocp.auth_service.mappers.UserCredentialMapper;
import lombok.Builder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.lang.String;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Service
public class AuthService {
	private  UserCredentialRepository userCredentialRepository;
	private  UserCredentialMapper userCredentialMapper;
	private  PasswordEncoder passwordEncoder;
	private  JwtService jwtService;
	private  RefreshTokenService refreshTokenService;
	private RefreshTokenRepository refreshTokenRepository;
	private PasswordResetTokenRepository passwordResetTokenRepository;
	private EmailService emailService;
    public AuthResponse login(LoginRequest loginRequest){

		 UserCredential user = userCredentialRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()->  new UserNotFoundException("Email n'existe pas"));
		  if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPasswordHash())){
			  throw new InvalidCredentialsException("Le mot de passe incorrect");
		  }

		if (!user.getEnabled()) {
			throw new AccountDisabledException("Compte désactivé");
		}

		if (user.getAccountLocked()) {
			throw new AccountLockedException("Compte verrouillé");
		}
		user.setLastLogin(LocalDateTime.now());



		  UserCredential saved = userCredentialRepository.save(user);
String accesToken = jwtService.generateAccessToken(user);
	  RefreshTokenResponse refreshToken = refreshTokenService.createRefreshToken(user);
		return AuthResponse.builder()
			.accessToken(accesToken)
			.refreshToken(refreshToken.getRefreshToken())
			.tokenType("Bearer")
			.build();
	}
	public AuthResponse refreshToken(RefreshTokenRequest request){
		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken()).orElseThrow(()-> new RuntimeException("La session est expiré"));
		if(refreshToken.isRevoked()){
			throw new RuntimeException("Le token est revoqué");
		}
		if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Le refresh token est expiré");
		}
		UserCredential user = refreshToken.getUser();
		String accessToken = jwtService.generateAccessToken(user);
		System.out.println(refreshToken.getRefreshToken());
		return AuthResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken.getRefreshToken())

			.build();
	}
	public void logout(LogoutRequest request){
		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken()).orElseThrow(() -> new RuntimeException("Refresh token introuvable"));
		refreshToken.setRevoked(true);
		refreshTokenRepository.save(refreshToken);



	}
 public 	void changePassword(UUID credentialId, ChangePasswordRequest request){
		// Verifier l'id existante
		UserCredential user = userCredentialRepository.findById(credentialId).orElseThrow(() -> new RuntimeException("introuvable"));
		if (!user.getEnabled()) {
			throw new RuntimeException("Compte désactivé");
		}

		if (user.getAccountLocked()) {
			throw new RuntimeException("Compte verrouillé");
		}
		if(!passwordEncoder.matches(request.getCurrentPassword(),user.getPasswordHash())){
			throw new RuntimeException("Le mot de passe incorrect");
		}
		if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
			throw new RuntimeException("Le nouveau mot de passe doit être différent de l'ancien");
		}
		user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
		userCredentialRepository.save(user);

	}
	 public void  forgotPassword(ForgotPasswordRequest request){
		UserCredential user = userCredentialRepository.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException("Compte verrouillé"));
		if (!user.getEnabled()) {
			throw new RuntimeException("Compte désactivé");
		}

		if (user.getAccountLocked()) {
			throw new RuntimeException("Compte verrouillé");
		}
		//token aleatoire
		String token = jwtService.generateRefreshToken(user);

		PasswordResetToken passwordResetToken = PasswordResetToken.builder()
			.token(token)
			.user(user)
			.expiryDate(LocalDateTime.now().plusMinutes(30))
			.used(false)
			.createdAt(LocalDateTime.now())
			.build();

		passwordResetTokenRepository.save(passwordResetToken);

		emailService.sendResetPasswordEmail(user.getEmail(), token);



	}
	 public void resetPassword(ResetPasswordRequest resetPasswordRequest){
		PasswordResetToken passwordResetToken = passwordResetTokenRepository
			.findByToken(resetPasswordRequest.getToken())
			.orElseThrow(() -> new RuntimeException("Token invalide"));
		 System.out.println("Token reçu : " + resetPasswordRequest.getToken());
		if (passwordResetToken.getUsed()) {
			throw new RuntimeException("Ce token a déjà été utilisé");
		}
		if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Le token a expiré");
		}
		UserCredential user = passwordResetToken.getUser();
		if (passwordEncoder.matches(resetPasswordRequest.getNewPassword(),
			user.getPasswordHash())) {
			throw new RuntimeException("Le nouveau mot de passe doit être différent");
		}
		user.setPasswordHash(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
		user.setUpdatedAt(LocalDateTime.now());
		userCredentialRepository.save(user);
		passwordResetToken.setUsed(true);
		passwordResetTokenRepository.save(passwordResetToken);


		}



	}




