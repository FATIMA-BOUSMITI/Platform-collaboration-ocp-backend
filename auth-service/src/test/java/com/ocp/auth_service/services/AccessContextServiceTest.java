package com.ocp.auth_service.services;
import com.ocp.auth_service.entity.*;
import com.ocp.auth_service.repository.*;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class AccessContextServiceTest {
 @Mock UserCredentialRepository users; @Mock RefreshTokenRepository tokens; @InjectMocks AccessContextService service;
 UserCredential user; RefreshToken token; Claims claims;
 @BeforeEach void setup(){Role role=new Role();role.setName("ADMIN");user=UserCredential.builder().id(UUID.randomUUID()).userId(UUID.randomUUID()).email("admin@example.test").enabled(true).accountLocked(false).role(role).build();token=new RefreshToken();token.setId(UUID.randomUUID());token.setUser(user);token.setExpiryDate(LocalDateTime.now().plusHours(1));claims=Jwts.claims().add("userId",user.getUserId().toString()).add("sessionId",token.getId().toString()).build();when(users.findByUserId(user.getUserId())).thenReturn(Optional.of(user));}
 @Test void returnsCurrentRole(){when(tokens.findById(token.getId())).thenReturn(Optional.of(token));assertEquals("ADMIN",service.resolve(claims).role());}
 @Test void rejectsRevokedSession(){token.setRevoked(true);when(tokens.findById(token.getId())).thenReturn(Optional.of(token));assertThrows(JwtException.class,()->service.resolve(claims));}
 @Test void rejectsExpiredSession(){token.setExpiryDate(LocalDateTime.now().minusSeconds(1));when(tokens.findById(token.getId())).thenReturn(Optional.of(token));assertThrows(JwtException.class,()->service.resolve(claims));}
 @Test void rejectsDisabledAccount(){user.setEnabled(false);assertThrows(JwtException.class,()->service.resolve(claims));verifyNoInteractions(tokens);}
 @Test void rejectsLockedAccount(){user.setAccountLocked(true);assertThrows(JwtException.class,()->service.resolve(claims));verifyNoInteractions(tokens);}
 @Test void rejectsAnotherUsersSession(){UserCredential other=UserCredential.builder().id(UUID.randomUUID()).build();token.setUser(other);when(tokens.findById(token.getId())).thenReturn(Optional.of(token));assertThrows(JwtException.class,()->service.resolve(claims));}
}
