package com.ocp.auth_service.services;

import com.ocp.auth_service.entity.UserCredential;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
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

	public String generateAccessToken(UserCredential user) {
 return generateAccessToken(user, null);
 }
 public String generateAccessToken(UserCredential user, UUID sessionId) {

		return Jwts.builder()
			.subject(user.getEmail())
			.claim("userId", user.getUserId().toString())
			.claim("role", user.getRole().getName())
            .claim("sessionId", sessionId == null ? null : sessionId.toString())
			.claim("type", "ACCESS")
			.claim("jti", UUID.randomUUID().toString())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + accessExpiration))
			.signWith(getSignInKey())
			.compact();
	}

	public String generateRefreshToken(UserCredential user) {

		return Jwts.builder()
			.subject(user.getEmail())
			.claim("userId", user.getUserId().toString())
			.claim("type", "REFRESH")
			.claim("jti", UUID.randomUUID().toString())
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

	public Claims parseAccessToken(String token) {
		Claims claims = extractClaims(token);
		if (!"ACCESS".equals(claims.get("type", String.class))) {
			throw new io.jsonwebtoken.JwtException("Token is not an access token");
		}
		String role = claims.get("role", String.class);
		if (claims.getSubject() == null || role == null || role.isBlank()) {
			throw new io.jsonwebtoken.JwtException("Required access-token claims are missing");
		}
		return claims;
	}

	private Claims extractClaims(String token) {

		return Jwts.parser()
			.verifyWith(getSignInKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
	public String extractTokenType(String token) {
		return extractClaims(token).get("type", String.class);
	}

}
