package com.ocp.auth_service.services;
import com.ocp.auth_service.entity.*;
import com.ocp.auth_service.repository.*;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.LocalDateTime;
@Service @RequiredArgsConstructor
public class AccessContextService {
 private final UserCredentialRepository users;
 private final RefreshTokenRepository tokens;
 @Transactional(readOnly=true)
 public Context resolve(Claims claims){
  UserCredential u=users.findByUserId(UUID.fromString(claims.get("userId",String.class))).orElseThrow(() -> new JwtException("Compte introuvable"));
  if(!Boolean.TRUE.equals(u.getEnabled()) || Boolean.TRUE.equals(u.getAccountLocked())) throw new JwtException("Compte indisponible");
  String sid=claims.get("sessionId",String.class);
  if(sid==null) throw new JwtException("Reconnectez-vous pour créer une session sécurisée");
  RefreshToken t=tokens.findById(UUID.fromString(sid)).orElseThrow(() -> new JwtException("Session introuvable"));
  if(t.isRevoked() || !t.getExpiryDate().isAfter(LocalDateTime.now()) || !t.getUser().getId().equals(u.getId()))throw new JwtException("Session révoquée");
  return new Context(u.getUserId(),u.getEmail(),u.getRole().getName(),u.getRole().getPermissions().stream().map(Permission::getName).toList());
 }
 public record Context(UUID userId,String email,String role,List<String> permissions){}
}
