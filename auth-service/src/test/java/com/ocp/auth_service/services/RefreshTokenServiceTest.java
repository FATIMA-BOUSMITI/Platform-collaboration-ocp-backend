package com.ocp.auth_service.services;

import com.ocp.auth_service.entity.RefreshToken;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.mappers.RefreshTokenMapper;
import com.ocp.auth_service.repository.RefreshTokenRepository;
import com.ocp.auth_service.repository.UserCredentialRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    @Mock RefreshTokenRepository repository;
    @Mock JwtService jwtService;
    @Mock RefreshTokenMapper mapper;
    @Mock UserCredentialRepository users;
    @InjectMocks RefreshTokenService service;

    @Test
    void secondLoginKeepsTheFirstSessionValid() {
        UserCredential user = new UserCredential();
        List<RefreshToken> sessions = new ArrayList<>();
        when(jwtService.generateRefreshToken(user)).thenReturn("session-one", "session-two");
        when(repository.save(any(RefreshToken.class))).thenAnswer(call -> {
            RefreshToken session = call.getArgument(0);
            sessions.add(session);
            return session;
        });
        service.createRefreshToken(user);
        service.createRefreshToken(user);
        assertThat(sessions).hasSize(2);
        assertThat(sessions).allSatisfy(session -> {
            assertThat(session.isRevoked()).isFalse();
            assertThat(session.getUser()).isSameAs(user);
            assertThat(session.getExpiryDate()).isAfter(LocalDateTime.now());
        });
        when(repository.findByRefreshToken("session-one")).thenReturn(Optional.of(sessions.get(0)));
        when(repository.findByRefreshToken("session-two")).thenReturn(Optional.of(sessions.get(1)));
        assertThat(service.isValid("session-one")).isTrue();
        assertThat(service.isValid("session-two")).isTrue();
    }

    @Test
    void explicitRevocationStillInvalidatesTheSelectedSession() {
        RefreshToken selected = new RefreshToken();
        selected.setExpiryDate(LocalDateTime.now().plusDays(1));
        when(repository.findByRefreshToken("selected")).thenReturn(Optional.of(selected));
        service.revokeToken("selected");
        assertThat(service.isValid("selected")).isFalse();
        verify(repository).save(selected);
    }

    @Test
    void expiredSessionRemainsInvalid() {
        RefreshToken expired = new RefreshToken();
        expired.setExpiryDate(LocalDateTime.now().minusSeconds(1));
        when(repository.findByRefreshToken("expired")).thenReturn(Optional.of(expired));
        assertThat(service.isValid("expired")).isFalse();
    }
}