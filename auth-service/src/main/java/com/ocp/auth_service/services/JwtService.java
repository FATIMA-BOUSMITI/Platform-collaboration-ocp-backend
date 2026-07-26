package com.ocp.auth_service.services;

import com.ocp.auth_service.entity.UserCredential;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.access-expiration}")
	private long accessExpiration;

	@Value("${jwt.refresh-expiration}")
	private long refreshExpiration;

	@Autowired
	private ConfigurableEnvironment environment;

	@PostConstruct
	public void verifySecret() {

		System.out.println("====================================");
		System.out.println("JWT Secret = " + secret);
		System.out.println("JWT Length = " + secret.length());
		System.out.println("====================================");

		for (PropertySource<?> ps : environment.getPropertySources()) {

			Object value = ps.getProperty("jwt.secret");

			if (value != null) {
				System.out.println(ps.getName() + " ---> " + value);
			}
		}

		System.out.println("====================================");
	}

	public String generateAccessToken(UserCredential user) {

		return Jwts.builder()
			.subject(user.getEmail())
			.claim("userId", user.getUserId().toString())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + accessExpiration))
			.signWith(getSignInKey())
			.compact();
	}

	public String generateRefreshToken(UserCredential user) {

		return Jwts.builder()
			.subject(user.getEmail())
			.claim("userId", user.getUserId().toString())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + refreshExpiration))
			.signWith(getSignInKey())
			.compact();
	}

	private SecretKey getSignInKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String extractEmail(String token) {
		return extractClaims(token).getSubject();
	}

	public UUID extractUserId(String token) {
		String id = extractClaims(token).get("userId", String.class);
		return UUID.fromString(id);
	}

	public Date extractExpiration(String token) {
		return extractClaims(token).getExpiration();
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public boolean isTokenValid(String token) {
		return !isTokenExpired(token);
	}

	private Claims extractClaims(String token) {

		return Jwts.parser()
			.verifyWith(getSignInKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
