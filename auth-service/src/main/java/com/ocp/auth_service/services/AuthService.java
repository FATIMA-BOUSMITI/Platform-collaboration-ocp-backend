package com.ocp.auth_service.services;

import com.ocp.auth_service.repository.PasswordResetTokenRepository;
import com.ocp.auth_service.repository.RefreshTokenRepository;
import com.ocp.auth_service.repository.UserCredentialRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.String;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {
	private  final UserCredentialRepository userCredentialRepository;
	private final   PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final EmailService emailService;
	private final com.ocp.auth_service.repository.LoginHistoryRepository history;
 private final com.ocp.auth_service.repository.AdminNoticeRepository notices;
 private final SecureRandom secureRandom = new SecureRandom();

    public AuthResponse login(LoginRequest request){return loginWithContext(request,"unknown","unknown");}
 public AuthResponse loginWithContext(LoginRequest loginRequest,String ip,String device){

		 UserCredential user = userCredentialRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()->  new UserNotFoundException("Identifiants invalides. Veuillez vérifier votre adresse e-mail et votre mot de passe."));
		  if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPasswordHash())){

			  user.setFailedAttempts(user.getFailedAttempts()+1);
 userCredentialRepository.save(user);
 recordLogin(user,false,ip,device);
 throw new InvalidCredentialsException("Identifiants invalides.");
		  }

		if (!user.getEnabled()) {
			throw new AccountDisabledException("Compte désactivé");
		}

		if (user.getAccountLocked()) {
			throw new AccountLockedException("Compte verrouillé");
		}
		user.setLastLogin(LocalDateTime.now());
 user.setFailedAttempts(0);



		  UserCredential saved = userCredentialRepository.save(user);

	  RefreshTokenResponse refreshToken = refreshTokenService.createRefreshToken(user);
 var session = refreshTokenRepository.findByRefreshToken(refreshToken.getRefreshToken()).orElseThrow();
 String accesToken = jwtService.generateAccessToken(user,session.getId());
 recordLogin(user,true,ip,device);
		return AuthResponse.builder()
			.accessToken(accesToken)
			.refreshToken(refreshToken.getRefreshToken())
			.tokenType("Bearer")
			.build();
	}
	@Transactional
 public AuthResponse refreshToken(RefreshTokenRequest request){
		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken()).orElseThrow(()-> new com.ocp.auth_service.exception.InvalidRefreshTokenException("Session expirée"));
		if(refreshToken.isRevoked()){
			throw new com.ocp.auth_service.exception.InvalidRefreshTokenException("Session révoquée");
		}
		if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new com.ocp.auth_service.exception.InvalidRefreshTokenException("Session expirée");
		}
		UserCredential user = refreshToken.getUser();
		if(!Boolean.TRUE.equals(user.getEnabled()) || Boolean.TRUE.equals(user.getAccountLocked())) throw new com.ocp.auth_service.exception.InvalidRefreshTokenException("Compte indisponible");
 String accessToken = jwtService.generateAccessToken(user, refreshToken.getId());
		
		return AuthResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken.getRefreshToken())

			.build();
	}
	public void logout(LogoutRequest request, String authenticatedEmail){
		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken()).orElseThrow(() -> new RuntimeException("Refresh token introuvable"));
		if (authenticatedEmail != null
			&& !refreshToken.getUser().getEmail().equalsIgnoreCase(authenticatedEmail)) {
			throw new AccessDeniedException("A user can only revoke their own session");
		}
		refreshToken.setRevoked(true);
		refreshTokenRepository.save(refreshToken);



	}
 public 	void changePassword(
		UUID credentialId,
		ChangePasswordRequest request,
		String authenticatedEmail){
		// Verifier l'id existante
		UserCredential user = userCredentialRepository.findById(credentialId).orElseThrow(() -> new RuntimeException("introuvable"));
		if (authenticatedEmail != null
			&& !user.getEmail().equalsIgnoreCase(authenticatedEmail)) {
			throw new AccessDeniedException("A user can only change their own password");
		}
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
	@Transactional
	public void forgotPassword(ForgotPasswordRequest request) {
		userCredentialRepository.findByEmail(request.getEmail()).ifPresent(user -> {
			String token = generatePasswordResetToken();
			passwordResetTokenRepository.deleteByUserAndUsedFalse(user);

			PasswordResetToken passwordResetToken = PasswordResetToken.builder()
				.token(hashPasswordResetToken(token))
				.user(user)
				.expiryDate(LocalDateTime.now().plusMinutes(30))
				.used(false)
				.createdAt(LocalDateTime.now())
				.build();

			passwordResetTokenRepository.save(passwordResetToken);
			emailService.sendResetPasswordEmail(user.getEmail(), token);
		});
	}

	@Transactional
	public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
		if (!Objects.equals(
			resetPasswordRequest.getNewPassword(),
			resetPasswordRequest.getConfirmedPassword()
		)) {
			throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
		}

		PasswordResetToken passwordResetToken = passwordResetTokenRepository
			.findByToken(hashPasswordResetToken(resetPasswordRequest.getToken()))
			.orElseThrow(() -> new RuntimeException("Token invalide"));

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
		user.setAccountLocked(false);
		user.setFailedAttempts(0);
		userCredentialRepository.save(user);
		refreshTokenRepository.deleteByUser(user);
		passwordResetToken.setUsed(true);
		passwordResetTokenRepository.save(passwordResetToken);
	}

 @Transactional(readOnly=true)
 public java.util.Map<String,Object> me(String email){
  var u=userCredentialRepository.findByEmail(email).orElseThrow();
  return java.util.Map.of("credentialId",u.getId(),"userId",u.getUserId(),"email",u.getEmail(),"enabled",u.getEnabled(),"accountLocked",u.getAccountLocked(),"role",u.getRole().getName());
 }
 private void recordLogin(UserCredential user,boolean success,String ip,String device){
  String safeDevice=device==null ? "unknown" : device.substring(0,Math.min(250,device.length()));
  history.save(com.ocp.auth_service.entity.LoginHistory.builder().user(user).success(success).loginDate(LocalDateTime.now()).ipAddress(ip==null ? "unknown" : ip).device(safeDevice).build());
  if(!success){
   var notice=new com.ocp.auth_service.entity.AdminNotice();notice.setOwner(user.getId());notice.setMessage("Tentative de connexion échouée sur votre compte.");notices.save(notice);
  }
 }

	private String generatePasswordResetToken() {
		byte[] tokenBytes = new byte[32];
		secureRandom.nextBytes(tokenBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
	}

	private String hashPasswordResetToken(String token) {
		try {
			byte[] hash = MessageDigest.getInstance("SHA-256")
				.digest(token.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 is not available", ex);
		}
	}
}
