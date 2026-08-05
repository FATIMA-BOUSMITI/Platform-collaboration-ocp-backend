package com.ocp.auth_service.controllers;

import com.ocp.auth_service.dto.request.*;
import com.ocp.auth_service.dto.response.AuthResponse;
import com.ocp.auth_service.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ocp.auth_service.dto.request.ChangePasswordRequest;
import com.ocp.auth_service.dto.request.ResetPasswordRequest;
import java.util.UUID;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthService authService;


	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
		@Valid @RequestBody LoginRequest request) {
        System.out.println("==== LOGIN CONTROLLER APPELE ====");
		AuthResponse response = authService.login(request);

		return ResponseEntity.ok(response);
	}
	@PostMapping("/logout")
	public ResponseEntity<?> logout(
		@RequestBody LogoutRequest request
	){

		authService.logout(request);

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
	public ResponseEntity<? > forgotPassword(
		@RequestBody ForgotPasswordRequest request) {

		authService.forgotPassword(request);

		return ResponseEntity.ok(
			"success"
		);
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
		@Valid @RequestBody ChangePasswordRequest request) {

		authService.changePassword(credentialId, request);

		return ResponseEntity.ok("Mot de passe modifié avec succès.");
	}


}
