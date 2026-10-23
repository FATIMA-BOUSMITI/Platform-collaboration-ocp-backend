package com.ocp.communication_service.controller;
import com.ocp.communication_service.service.NotificationService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import java.nio.charset.StandardCharsets;
@RestController @RequestMapping("/api/internal/collaboration/events")
public class EventIngestionController {
 private final NotificationService service;private final javax.crypto.SecretKey key;
 public EventIngestionController(NotificationService service,@Value("${jwt.secret}")String secret){this.service=service;key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
 @PostMapping public void receive(@RequestHeader("Authorization")String authorization,@RequestBody NotificationService.Event event){try{if(!authorization.startsWith("Bearer "))throw new JwtException("Token requis");var claims=Jwts.parser().verifyWith(key).build().parseSignedClaims(authorization.substring(7)).getPayload();if(!"COLLABORATION_SERVICE".equals(claims.get("type",String.class))||!"project-management-service".equals(claims.getIssuer())||!"communication-service".equals(claims.get("serviceAudience",String.class)))throw new JwtException("Émetteur non autorisé");}catch(JwtException|IllegalArgumentException e){throw new AccessDeniedException("Événement non authentifié");}service.publish(event);}
}
