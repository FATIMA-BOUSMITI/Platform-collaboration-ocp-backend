package com.ocp.organisation_service.security;

import com.ocp.organisation_service.controllers.UserProfileController;
import com.ocp.organisation_service.services.UserProfileService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@TestPropertySource(properties = {
    "jwt.secret=qa-security-test-signing-key-with-at-least-32-bytes"
})
class SecurityAuthorizationTest {

    private static final String JWT_SECRET =
        "qa-security-test-signing-key-with-at-least-32-bytes";


    @MockBean private AuthAccessClient access;
    @org.junit.jupiter.api.BeforeEach void context(){org.mockito.Mockito.when(access.resolve(org.mockito.ArgumentMatchers.anyString())).thenAnswer(inv->{String h=inv.getArgument(0);var c=Jwts.parser().verifyWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8))).build().parseSignedClaims(h.substring(7)).getPayload();return new AuthAccessClient.Access(UUID.fromString(c.get("userId",String.class)),c.getSubject(),c.get("role",String.class),java.util.List.of());});}
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    @Test
    void userManagementRejectsAnonymousRequests() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void userManagementRejectsNonAdminTokens() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", bearerToken("EMPLOYEE", UUID.randomUUID())))
            .andExpect(status().isForbidden());
    }

    @Test
    void userManagementAllowsAdminTokens() throws Exception {
        when(userProfileService.getAllUsers()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/users")
                .header("Authorization", bearerToken("ADMIN", UUID.randomUUID())))
            .andExpect(status().isOk());
    }

    @Test
    void authenticatedUsersCanReadTheirOwnProfile() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userProfileService.getUserByAuthUserId(userId)).thenReturn(null);

        mockMvc.perform(get("/api/users/auth/{authUserId}", userId)
                .header("Authorization", bearerToken("EMPLOYEE", userId)))
            .andExpect(status().isOk());
    }

    @Test
    void authenticatedUsersCannotReadAnotherProfile() throws Exception {
        UUID requestedUserId = UUID.randomUUID();

        mockMvc.perform(get("/api/users/auth/{authUserId}", requestedUserId)
                .header("Authorization", bearerToken("EMPLOYEE", UUID.randomUUID())))
            .andExpect(status().isForbidden());
    }

    private String bearerToken(String role, UUID userId) {
        SecretKey signingKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        return "Bearer " + Jwts.builder()
            .subject("qa-security@example.test")
            .claim("userId", userId.toString())
            .claim("role", role)
            .claim("type", "ACCESS")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 60_000))
            .signWith(signingKey)
            .compact();
    }
}
