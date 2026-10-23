package com.ocp.communication_service.security;

import com.ocp.communication_service.controller.ConversationController;
import com.ocp.communication_service.service.ConversationService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@TestPropertySource(properties = {
    "jwt.secret=qa-security-test-signing-key-with-at-least-32-bytes"
})
class SecurityAuthorizationTest {

    private static final String JWT_SECRET =
        "qa-security-test-signing-key-with-at-least-32-bytes";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService conversationService;

    @MockBean
    private com.ocp.communication_service.realtime.AuthContextClient authContextClient;

    @BeforeEach
    void resolveAuthenticationContext() {
        when(authContextClient.resolve(anyString())).thenAnswer(invocation -> {
            String bearer = invocation.getArgument(0);
            var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(bearer.substring(7))
                .getPayload();
            return new com.ocp.communication_service.realtime.AuthContextClient.Context(
                UUID.fromString(claims.get("userId", String.class)),
                claims.getSubject(),
                claims.get("role", String.class),
                List.of());
        });
    }

    @Test
    void conversationRoutesRejectAnonymousRequests() throws Exception {
        mockMvc.perform(get("/api/conversations/user/{userId}", UUID.randomUUID()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void usersCanListTheirOwnConversations() throws Exception {
        UUID userId = UUID.randomUUID();
        when(conversationService.getConversationsByUser(userId)).thenReturn(List.of());

        mockMvc.perform(get("/api/conversations/user/{userId}", userId)
                .header("Authorization", bearerToken(userId)))
            .andExpect(status().isOk());
    }

    @Test
    void usersCannotListAnotherUsersConversations() throws Exception {
        mockMvc.perform(get("/api/conversations/user/{userId}", UUID.randomUUID())
                .header("Authorization", bearerToken(UUID.randomUUID())))
            .andExpect(status().isForbidden());
    }

    private String bearerToken(UUID userId) {
        SecretKey signingKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        return "Bearer " + Jwts.builder()
            .subject("qa-security@example.test")
            .claim("userId", userId.toString())
            .claim("role", "EMPLOYEE")
            .claim("type", "ACCESS")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 60_000))
            .signWith(signingKey)
            .compact();
    }
}
