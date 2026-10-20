package com.ocp.auth_service.config;

import com.ocp.auth_service.security.JwtAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!local")
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST,
                    "/api/auth/login",
                    "/api/auth/refresh-token",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/users/*/roles").authenticated()
                .requestMatchers("/api/auth/admin/**").hasAnyRole("ADMIN", "ADMINISTRATOR", "ADMINISTRATEUR")
                .requestMatchers("/api/auth/users/**",
                    "/api/roles/**",
                    "/api/permissions/**").access(new com.ocp.auth_service.security.PermissionAccess())
                .requestMatchers("/api/auth/**").authenticated()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
