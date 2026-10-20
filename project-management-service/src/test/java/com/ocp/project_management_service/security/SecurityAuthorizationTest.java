package com.ocp.project_management_service.security;

import com.ocp.project_management_service.controller.ProjectController;
import com.ocp.project_management_service.service.ProjectService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
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
    private ProjectService projectService;

    @Test
    void projectRoutesRejectAnonymousRequests() throws Exception {
        mockMvc.perform(get("/api/projects"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void validAccessTokenReachesTheProjectService() throws Exception {
        when(projectService.getAllProjects(any(AuthenticatedUser.class)))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/projects")
                .header("Authorization", bearerToken(UUID.randomUUID())))
            .andExpect(status().isOk());
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
