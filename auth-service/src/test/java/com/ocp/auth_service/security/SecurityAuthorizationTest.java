package com.ocp.auth_service.security;

import com.ocp.auth_service.controllers.AuthenticationController;
import com.ocp.auth_service.controllers.UserController;
import com.ocp.auth_service.config.SecurityConfig;
import com.ocp.auth_service.entity.Role;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.services.AuthService;
import com.ocp.auth_service.services.JwtService;
import com.ocp.auth_service.services.RoleService;
import com.ocp.auth_service.services.UserService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.List.of;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthenticationController.class, UserController.class})
@Import({
    SecurityConfig.class,
    JwtAuthenticationFilter.class,
    JwtService.class
})
@TestPropertySource(properties = {
    "jwt.secret=qa-security-test-signing-key-with-at-least-32-bytes",
    "jwt.access-expiration=900000",
    "jwt.refresh-expiration=604800000"
})
class SecurityAuthorizationTest {


    @MockBean private com.ocp.auth_service.services.AccessContextService access;
    @org.junit.jupiter.api.BeforeEach void context(){org.mockito.Mockito.when(access.resolve(org.mockito.ArgumentMatchers.any())).thenAnswer(inv->{io.jsonwebtoken.Claims c=inv.getArgument(0);return new com.ocp.auth_service.services.AccessContextService.Context(UUID.fromString(c.get("userId",String.class)),c.getSubject(),c.get("role",String.class),java.util.List.of());});}
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private AuthService authService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private UserService userService;

    @Test
    void loginRemainsPublic() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isOk());
    }

    @Test
    void loginIgnoresStaleBearerTokenFromPreviousSession() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .header("Authorization", "Bearer expired-or-invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isOk());
    }

    @Test
    void credentialManagementRejectsAnonymousRequests() throws Exception {
        mockMvc.perform(get("/api/auth/users"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void credentialManagementRejectsNonAdminTokens() throws Exception {
        mockMvc.perform(get("/api/auth/users")
                .header("Authorization", bearerToken("EMPLOYEE")))
            .andExpect(status().isForbidden());
    }

    @Test
    void credentialManagementAllowsAdminTokens() throws Exception {
        when(userService.getAllUsers()).thenReturn(of());
        mockMvc.perform(get("/api/auth/users")
                .header("Authorization", bearerToken("ADMIN")))
            .andExpect(status().isOk());
    }

    @Test
    void authenticatedUsersCanReadTheirOwnRole() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(get("/api/auth/users/{userId}/roles", userId)
                .header("Authorization", bearerToken("EMPLOYEE", userId)))
            .andExpect(status().isOk());
    }

    @Test
    void authenticatedUsersCannotReadAnotherUsersRole() throws Exception {
        mockMvc.perform(get("/api/auth/users/{userId}/roles", UUID.randomUUID())
                .header("Authorization", bearerToken("EMPLOYEE", UUID.randomUUID())))
            .andExpect(status().isForbidden());
    }

    private String bearerToken(String roleName) {
        return bearerToken(roleName, UUID.randomUUID());
    }

    private String bearerToken(String roleName, UUID userId) {
        Role role = new Role();
        role.setName(roleName);
        UserCredential user = new UserCredential();
        user.setEmail("qa-security@example.test");
        user.setUserId(userId);
        user.setRole(role);
        return "Bearer " + jwtService.generateAccessToken(user);
    }
}
