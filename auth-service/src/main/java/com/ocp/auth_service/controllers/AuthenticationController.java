package com.ocp.auth_service.controllers;

import com.ocp.auth_service.dto.request.*;
import com.ocp.auth_service.dto.response.AuthResponse;
import com.ocp.auth_service.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.ocp.auth_service.security.AuthenticatedUser;
import com.ocp.auth_service.dto.request.ChangePasswordRequest;
import com.ocp.auth_service.dto.request.ResetPasswordRequest;
import java.util.UUID;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthService authService;
 private final com.ocp.auth_service.services.AccessContextService access;
 private final com.ocp.auth_service.services.JwtService jwt;
 @GetMapping("/access-context") public Object context(@RequestHeader("Authorization") String header){return access.resolve(jwt.parseAccessToken(header.substring(7)));}
 @GetMapping("/me") public Object me(@AuthenticationPrincipal AuthenticatedUser u){return authService.me(u.email());}


	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
		@Valid @RequestBody LoginRequest request, jakarta.servlet.http.HttpServletRequest servletRequest) {
        
		AuthResponse response = authService.loginWithContext(request, servletRequest.getRemoteAddr(), servletRequest.getHeader("User-Agent"));

		return ResponseEntity.ok(response);
	}
	@PostMapping("/logout")
	public ResponseEntity<?> logout(
		@RequestBody LogoutRequest request,
		@AuthenticationPrincipal(errorOnInvalidType = false) AuthenticatedUser authenticatedUser
	){

		authService.logout(request,
			authenticatedUser == null ? null : authenticatedUser.email());

		return ResponseEntity.ok(
			"Logout successful"
		);
	}
	@PostMapping("/refresh-token")
	public ResponseEntity<AuthResponse> refreshToken(
		@RequestBody RefreshTokenRequest request) {

		AuthResponse response = authService.refreshToken(request);

		return ResponseEntity.ok(response);
	}
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(
		@Valid @RequestBody ForgotPasswordRequest request) {

		authService.forgotPassword(request);

		return ResponseEntity.ok("Si cette adresse correspond à un compte, un lien de réinitialisation a été envoyé.");
	}
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(
		@Valid @RequestBody ResetPasswordRequest request) {

		authService.resetPassword(request);

		return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
	}
	@PostMapping("/change-password/{credentialId}")
	public ResponseEntity<String> changePassword(
		@PathVariable UUID credentialId,
		@Valid @RequestBody ChangePasswordRequest request,
		@AuthenticationPrincipal(errorOnInvalidType = false) AuthenticatedUser authenticatedUser) {

		authService.changePassword(credentialId, request,
			authenticatedUser == null ? null : authenticatedUser.email());

		return ResponseEntity.ok("Mot de passe modifié avec succès.");
	}


}
