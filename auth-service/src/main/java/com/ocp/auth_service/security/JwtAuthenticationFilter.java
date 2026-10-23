package com.ocp.auth_service.security;

import com.ocp.auth_service.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Profile("!local")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final com.ocp.auth_service.services.AccessContextService access;

    public JwtAuthenticationFilter(JwtService jwtService, com.ocp.auth_service.services.AccessContextService access) {
        this.access = access;
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        if (!HttpMethod.POST.matches(request.getMethod())) {
            return false;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.equals("/api/auth/login")
            || path.equals("/api/auth/refresh-token")
            || path.equals("/api/auth/forgot-password")
            || path.equals("/api/auth/reset-password");
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
            Claims claims = jwtService.parseAccessToken(authorization.substring(7).trim());
            var context = access.resolve(claims);
            String role = context.role().trim().toUpperCase();
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            String userIdClaim = claims.get("userId", String.class);
            if (userIdClaim == null) {
                throw new JwtException("Required access-token claims are missing");
            }
            AuthenticatedUser principal = new AuthenticatedUser(
                context.email(), UUID.fromString(userIdClaim), role);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    java.util.stream.Stream.concat(java.util.stream.Stream.of(authority), context.permissions().stream()).map(SimpleGrantedAuthority::new).toList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Bearer token is invalid or expired");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
