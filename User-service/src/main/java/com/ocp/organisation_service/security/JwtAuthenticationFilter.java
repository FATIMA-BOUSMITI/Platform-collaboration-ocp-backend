package com.ocp.organisation_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
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

    private final SecretKey signingKey;
 private final AuthAccessClient accessClient;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret, AuthAccessClient accessClient) {
 this.accessClient=accessClient;
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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
            String token = authorization.substring(7).trim();
            Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            if (!"ACCESS".equals(claims.get("type", String.class))) {
                throw new JwtException("Token is not an access token");
            }
            AuthAccessClient.Access context=accessClient.resolve(authorization);
 if(context==null || !context.userId().toString().equals(claims.get("userId",String.class)))throw new JwtException("Session indisponible");
 String email = context.email();
            String role = context.role();
            String userIdClaim = claims.get("userId", String.class);
            if (email == null || role == null || role.isBlank() || userIdClaim == null) {
                throw new JwtException("Required access-token claims are missing");
            }
            AuthenticatedUser principal =
                new AuthenticatedUser(email, UUID.fromString(userIdClaim), role);
            role = role.trim().toUpperCase();
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    principal, null, java.util.stream.Stream.concat(java.util.stream.Stream.of(authority), context.permissions().stream()).map(SimpleGrantedAuthority::new).toList()));
        } catch (org.springframework.web.client.ResourceAccessException exception) {
 response.sendError(503,"Service authentification indisponible");return;
 } catch (org.springframework.web.client.RestClientResponseException exception) {
 response.sendError(exception.getStatusCode().is4xxClientError()?401:503,"Session indisponible");return;
 } catch (JwtException | IllegalArgumentException exception) {
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
