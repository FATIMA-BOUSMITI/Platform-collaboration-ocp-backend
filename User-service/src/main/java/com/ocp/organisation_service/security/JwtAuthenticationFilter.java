package com.ocp.organisation_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Profile("!local")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final AuthAccessClient accessClient;

    public JwtAuthenticationFilter(AuthAccessClient accessClient) {
        this.accessClient = accessClient;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            AuthAccessClient.Access context = accessClient.resolve(authorization);
            if (context == null || context.userId() == null || context.email() == null
                || context.role() == null || context.role().isBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bearer token is invalid or expired");
                return;
            }
            String role = context.role().trim().toUpperCase();
            AuthenticatedUser principal = new AuthenticatedUser(context.email(), context.userId(), role);
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    principal, null, java.util.stream.Stream.concat(java.util.stream.Stream.of(authority), context.permissions().stream()).map(SimpleGrantedAuthority::new).toList()));
        } catch (org.springframework.web.client.ResourceAccessException exception) {
 response.sendError(503,"Service authentification indisponible");return;
 } catch (org.springframework.web.client.RestClientResponseException exception) {
 response.sendError(exception.getStatusCode().is4xxClientError()?401:503,"Session indisponible");return;
 } catch (IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            LOGGER.warn("Rejected access token for {}: {}",
                request.getRequestURI(), exception.getClass().getSimpleName());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Bearer token is invalid or expired");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
