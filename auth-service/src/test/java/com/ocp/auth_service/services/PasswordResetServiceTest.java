package com.ocp.auth_service.services;

import com.ocp.auth_service.dto.request.ForgotPasswordRequest;
import com.ocp.auth_service.dto.request.ResetPasswordRequest;
import com.ocp.auth_service.entity.PasswordResetToken;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.repository.PasswordResetTokenRepository;
import com.ocp.auth_service.repository.RefreshTokenRepository;
import com.ocp.auth_service.repository.UserCredentialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @Test
    void forgotPassword_sendsOneTimeTokenAndStoresOnlyItsHash() throws Exception {
        UserCredential user = userCredential();
        when(userCredentialRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(user.getEmail());
        authService.forgotPassword(request);

        ArgumentCaptor<String> rawTokenCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<PasswordResetToken> storedTokenCaptor =
            ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(emailService).sendResetPasswordEmail(eq(user.getEmail()), rawTokenCaptor.capture());
        verify(passwordResetTokenRepository).save(storedTokenCaptor.capture());
        verify(passwordResetTokenRepository).deleteByUserAndUsedFalse(user);

        String rawToken = rawTokenCaptor.getValue();
        PasswordResetToken storedToken = storedTokenCaptor.getValue();
        assertThat(rawToken).hasSize(43);
        assertThat(storedToken.getToken()).isEqualTo(sha256(rawToken));
        assertThat(storedToken.getToken()).isNotEqualTo(rawToken);
        assertThat(storedToken.getExpiryDate()).isAfter(LocalDateTime.now());
    }

    @Test
    void forgotPassword_doesNotRevealUnknownEmail() {
        when(userCredentialRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("unknown@example.com");
        authService.forgotPassword(request);

        verifyNoInteractions(passwordResetTokenRepository, emailService);
    }

    @Test
    void resetPassword_updatesPasswordUnlocksAccountAndRevokesSessions() throws Exception {
        UserCredential user = userCredential();
        user.setPasswordHash("old-hash");
        user.setAccountLocked(true);
        user.setFailedAttempts(5);

        String rawToken = "one-time-reset-token";
        PasswordResetToken resetToken = PasswordResetToken.builder()
            .token(sha256(rawToken))
            .user(user)
            .expiryDate(LocalDateTime.now().plusMinutes(10))
            .used(false)
            .createdAt(LocalDateTime.now())
            .build();
        when(passwordResetTokenRepository.findByToken(sha256(rawToken)))
            .thenReturn(Optional.of(resetToken));
        when(passwordEncoder.matches("new-password", "old-hash")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(rawToken);
        request.setNewPassword("new-password");
        request.setConfirmedPassword("new-password");
        authService.resetPassword(request);

        assertThat(user.getPasswordHash()).isEqualTo("new-hash");
        assertThat(user.getAccountLocked()).isFalse();
        assertThat(user.getFailedAttempts()).isZero();
        assertThat(resetToken.getUsed()).isTrue();
        verify(refreshTokenRepository).deleteByUser(user);
        verify(userCredentialRepository).save(user);
        verify(passwordResetTokenRepository).save(resetToken);
    }

    @Test
    void resetPassword_rejectsMismatchedConfirmationBeforeTokenLookup() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("one-time-reset-token");
        request.setNewPassword("new-password");
        request.setConfirmedPassword("different-password");

        assertThatThrownBy(() -> authService.resetPassword(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Les mots de passe ne correspondent pas.");
        verifyNoInteractions(passwordResetTokenRepository);
    }

    @Test
    void emailService_buildsResetLinkForConfiguredFrontend() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService emailService = new EmailService(mailSender);
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:5173");
        ReflectionTestUtils.setField(emailService, "senderAddress", "no-reply@example.com");

        emailService.sendResetPasswordEmail("user@example.com", "token+with/slash");

        ArgumentCaptor<SimpleMailMessage> messageCaptor =
            ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getFrom()).isEqualTo("no-reply@example.com");
        assertThat(message.getText()).contains(
            "http://localhost:5173/reset-password?token=token%2Bwith%2Fslash"
        );
    }

    private static UserCredential userCredential() {
        UserCredential user = new UserCredential();
        user.setEmail("user@example.com");
        user.setEnabled(false);
        user.setAccountLocked(false);
        user.setFailedAttempts(0);
        return user;
    }

    private static String sha256(String token) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256")
            .digest(token.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}
