package com.ocp.auth_service.services;
import com.ocp.auth_service.entity.*;
import com.ocp.auth_service.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest(properties={"spring.jpa.hibernate.ddl-auto=create-drop","logging.level.root=WARN","debug=false"})
@Import({AdminService.class,AccessContextService.class})
class AdminPersistenceTest {
 @Autowired AdminService admin; @Autowired AccessContextService access; @Autowired RoleRepository roles; @Autowired UserCredentialRepository users; @Autowired RefreshTokenRepository tokens; @Autowired TestEntityManager em;
 UserCredential user;
 @BeforeEach void setup(){Role role=new Role();role.setName("ADMIN");roles.save(role);user=UserCredential.builder().userId(UUID.randomUUID()).email("admin-persistence@example.test").enabled(true).accountLocked(false).failedAttempts(0).passwordHash("test-hash").role(role).build();users.save(user);em.flush();}
 @Test void preferenceSurvivesReload(){admin.settings(user.getEmail(),Map.of("language","fr","timezone","Africa/Casablanca"));em.flush();em.clear();assertEquals("Africa/Casablanca",admin.settings(user.getEmail()).get("timezone"));}
 @Test void sessionRevocationSurvivesReload(){RefreshToken token=new RefreshToken();token.setRefreshToken(UUID.randomUUID().toString());token.setUser(user);token.setCreatedAt(LocalDateTime.now());token.setExpiryDate(LocalDateTime.now().plusHours(1));tokens.saveAndFlush(token);UUID id=token.getId();admin.revoke(id,user.getEmail(),true);em.flush();em.clear();assertTrue(tokens.findById(id).orElseThrow().isRevoked());assertEquals(0L,admin.stats().get("activeSessions"));}
 @Test void rejectsInvalidTimezoneWithoutPersisting(){assertThrows(IllegalArgumentException.class,()->admin.settings(user.getEmail(),Map.of("language","fr","timezone","invalid-zone")));}
}
