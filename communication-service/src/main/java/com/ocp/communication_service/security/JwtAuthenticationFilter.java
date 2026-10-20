package com.ocp.communication_service.security;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Profile("!local")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey signingKey;
    private final com.ocp.communication_service.realtime.AuthContextClient auth;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret,com.ocp.communication_service.realtime.AuthContextClient auth) {
        this.auth=auth;
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override protected boolean shouldNotFilter(HttpServletRequest request){return request.getRequestURI().equals("/api/internal/collaboration/events");}

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
            String role = claims.get("role", String.class);
            String userIdClaim = claims.get("userId", String.class);
            if (!"ACCESS".equals(claims.get("type", String.class))
                || claims.getSubject() == null
                || role == null || role.isBlank()
                || userIdClaim == null) {
                throw new JwtException("Required access-token claims are missing");
            }
            var current=auth.resolve(authorization);
            if(current==null||!current.userId().toString().equals(userIdClaim))throw new JwtException("Session invalide");
            role=current.role();
            AuthenticatedUser principal = new AuthenticatedUser(current.email(),current.userId(),role);
            role = role.trim().toUpperCase();
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    principal, null, List.of(new SimpleGrantedAuthority(authority))));
        } catch (org.springframework.web.client.RestClientResponseException exception) {
            response.setStatus(exception.getStatusCode().is4xxClientError()?401:503);return;
        } catch (org.springframework.web.client.ResourceAccessException exception) {
            response.setStatus(503);return;
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Bearer token is invalid or expired");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
