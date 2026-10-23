package com.ocp.organisation_service;
import com.ocp.organisation_service.controllers.TeamController;
import com.ocp.organisation_service.entity.*;
import com.ocp.organisation_service.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest(properties={"spring.jpa.hibernate.ddl-auto=create-drop","logging.level.root=WARN","debug=false"}) @Import(TeamController.class)
class TeamPersistenceTest {
 @Autowired TeamController controller; @Autowired DepartementRepository departments; @Autowired UserProfileRepository users; @Autowired TeamMemberRepository members; @Autowired TeamRepository teams; @Autowired TestEntityManager em;
 Departement department; UserProfile user;
 @BeforeEach void setup(){department=new Departement();department.setName("Département de test");department.setCode("TEST");departments.save(department);user=new UserProfile();user.setId(UUID.randomUUID());user.setFirstName("Utilisateur");user.setLastName("Test");users.save(user);em.flush();}
 private TeamController.View create(){return controller.create(new TeamController.Request("Équipe de test",department.getId(),user.getId(),"Objectif de test",true));}
 @Test void teamAndMembersSurviveReload(){var team=create();controller.add(team.id(),user.getId());em.flush();em.clear();var loaded=controller.all().get(0);assertEquals("Équipe de test",loaded.name());assertEquals(List.of(user.getId()),loaded.memberIds());}
 @Test void repeatedMembershipAdditionIsIdempotent(){var team=create();controller.add(team.id(),user.getId());controller.add(team.id(),user.getId());em.flush();assertEquals(1,members.findByTeamId(team.id()).size());}
 @Test void deletingTeamRemovesMemberships(){var team=create();controller.add(team.id(),user.getId());controller.delete(team.id());em.flush();em.clear();assertFalse(teams.existsById(team.id()));assertTrue(members.findByTeamId(team.id()).isEmpty());}
 @Test void rejectsUnknownMember(){var team=create();assertThrows(org.springframework.web.server.ResponseStatusException.class,()->controller.add(team.id(),UUID.randomUUID()));}
}
