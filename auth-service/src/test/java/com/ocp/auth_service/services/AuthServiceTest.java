package com.ocp.auth_service.services;


import com.ocp.auth_service.repository.UserCredentialRepository;
import com.ocp.auth_service.dto.request.LoginRequest;
import com.ocp.auth_service.dto.response.AuthResponse;
import com.ocp.auth_service.dto.response.RefreshTokenResponse;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.exception.AccountDisabledException;
import com.ocp.auth_service.exception.AccountLockedException;
import com.ocp.auth_service.exception.InvalidCredentialsException;
import com.ocp.auth_service.exception.UserNotFoundException;
import lombok.Builder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//
//		@Mock
//		private UserCredentialRepository repository;
//
//		@Mock
//		private PasswordEncoder passwordEncoder;
//
//		@Mock
//		private JwtService jwtService;
//
//		@Mock
//		private RefreshTokenService refreshTokenService;
//
//		@InjectMocks
//		private AuthService authService;
//		@Test
////	    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
////			LoginRequest request = LoginRequest.builder()
////				.email("admin@ocp.com")
////				.password("password123")
////				.build();
////			//UserCredential user = UserCredential.builder()
////				.email("admin@ocp.com")
////				.passwordHash("encodedPassword")
////				.enabled(true)
////				.accountLocked(false)
////				.build();
////			RefreshTokenResponse refreshToken = RefreshTokenResponse.builder()
////				.refreshToken("refresh-token")
////				.build();
////
////			when(repository.findByEmail(request.getEmail()))
////				.thenReturn(Optional.of(user));
////
////			when(passwordEncoder.matches(
////				request.getPassword(),
////				user.getPasswordHash()))
////				.thenReturn(true);
////
////			when(repository.save(user))
////				.thenReturn(user);
////
////			when(jwtService.generateAccessToken(user))
////				.thenReturn("access-token");
////
////			when(refreshTokenService.createRefreshToken(user))
////				.thenReturn(refreshToken);
////
////			// Act
////			AuthResponse response = authService.login(request);
////
////			// Assert
////			assertNotNull(response);
////			assertEquals("access-token", response.getAccessToken());
////			assertEquals("refresh-token", response.getRefreshToken());
////			assertEquals("Bearer", response.getTokenType());
////
////			verify(repository).findByEmail(request.getEmail());
////			verify(passwordEncoder).matches(request.getPassword(), user.getPasswordHash());
////			verify(repository).save(user);
////			verify(jwtService).generateAccessToken(user);
////			verify(refreshTokenService).createRefreshToken(user);
////		}
////	@Test
////	void login_shouldThrowException_whenEmailDoesNotExist() {
////
////		// Arrange
////		LoginRequest request = LoginRequest.builder()
////			.email("unknown@gmail.com")
////			.password("password123")
////			.build();
////		UserCredential user = UserCredential.builder()
////			.email("admin@ocp.com")
////			.passwordHash("encodedPassword")
////			.enabled(true)
////			.accountLocked(false)
////			.build();
////
////		when(repository.findByEmail(request.getEmail()))
////			.thenReturn(Optional.empty());
////
////
////		// Act + Assert
////		assertThrows(UserNotFoundException.class, () -> {
////			authService.login(request);
////		});
////
////
////		// Verify
////		verify(repository).findByEmail(request.getEmail());
////
////		verifyNoInteractions(passwordEncoder);
////		verifyNoInteractions(jwtService);
////		verifyNoInteractions(refreshTokenService);
////	}
//	@Test
//	void login_shouldThrowException_whenInvalidCredentials() {
//
//		// Arrange
//		LoginRequest request = LoginRequest.builder()
//			.email("unknown@gmail.com")
//			.password("password123")
//			.build();
//		UserCredential user = UserCredential.builder()
//			.email("admin@ocp.com")
//			.passwordHash("encodedPassword")
//			.enabled(false)
//			.accountLocked(false)
//			.build();
//
//
//		when(repository.findByEmail(request.getEmail()))
//			.thenReturn(Optional.of(user));
//
//		when(passwordEncoder.matches(
//			request.getPassword(),
//			user.getPasswordHash()))
//			.thenReturn(false);
//
//
//		// Act + Assert
//		assertThrows(InvalidCredentialsException.class, () -> {
//			authService.login(request);
//		});
//
//
//		// Verify
//		verify(repository).findByEmail(request.getEmail());
//		verify(passwordEncoder).matches(request.getPassword(), user.getPasswordHash());
//
//
//		verifyNoInteractions(jwtService);
//		verifyNoInteractions(refreshTokenService);
//	}
//	@Test
//	void login_shouldThrowException_whenAccoutDisabledException() {
//
//		// Arrange
//		LoginRequest request = LoginRequest.builder()
//			.email("unknown@gmail.com")
//			.password("password123")
//			.build();
//		UserCredential user = UserCredential.builder()
//			.email("admin@ocp.com")
//			.passwordHash("encodedPassword")
//			.enabled(false)
//			.accountLocked(false)
//			.build();
//
//
//		when(repository.findByEmail(request.getEmail()))
//			.thenReturn(Optional.of(user));
//		when(passwordEncoder.matches(
//			request.getPassword(),
//			user.getPasswordHash()))
//			.thenReturn(true);
//
//
//		// Act + Assert
//		assertThrows(AccountDisabledException.class, () -> {
//			authService.login(request);
//		});
//
//
//		// Verify
//		verify(repository).findByEmail(request.getEmail());
//
//
//
//		verifyNoInteractions(jwtService);
//		verifyNoInteractions(refreshTokenService);
//	}
//	@Test
//	void login_shouldThrowException_whenAccoutLockedException() {
//
//		// Arrange
//		LoginRequest request = LoginRequest.builder()
//			.email("unknown@gmail.com")
//			.password("password123")
//			.build();
//		UserCredential user = UserCredential.builder()
//			.email("admin@ocp.com")
//			.passwordHash("encodedPassword")
//			.enabled(true)
//			.accountLocked(true)
//			.build();
//
//
//		when(repository.findByEmail(request.getEmail()))
//			.thenReturn(Optional.of(user));
//
//		when(passwordEncoder.matches(
//			request.getPassword(),
//			user.getPasswordHash()))
//			.thenReturn(true);
//
//
//		// Act + Assert
//		assertThrows(AccountLockedException.class, () -> {
//			authService.login(request);
//		});
//
//
//		// Verify
//		verify(repository).findByEmail(request.getEmail());
//
//
//
//		verifyNoInteractions(jwtService);
//		verifyNoInteractions(refreshTokenService);
//	}
//	}


