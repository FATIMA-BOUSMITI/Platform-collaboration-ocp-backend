package com.ocp.organisation_service.controllers;
import com.ocp.organisation_service.entity.*;
import com.ocp.organisation_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.time.LocalDateTime;
@RestController @RequestMapping("/api/users/teams") @RequiredArgsConstructor
public class TeamController {
 private final TeamRepository teams; private final TeamMemberRepository members; private final UserProfileRepository users; private final DepartementRepository departments;
 public record Request(String name,UUID departmentId,UUID responsableId,String objective,Boolean active){}
 public record View(UUID id,String name,UUID departmentId,String department,UUID responsableId,String chief,String objective,boolean active,List<UUID> memberIds){}
 private View view(Team t){return new View(t.getId(),t.getName(),t.getDepartment().getId(),t.getDepartment().getName(),t.getResponsableId(),t.getResponsableId()==null ? "" : users.findById(t.getResponsableId()).map(u->u.getFirstName()+" "+u.getLastName()).orElse("Responsable supprimé"),t.getObjective(),!Boolean.FALSE.equals(t.getActive()),members.findByTeamId(t.getId()).stream().map(TeamMember::getUserId).toList());}
 private Team find(UUID id){return teams.lockById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable"));}
 @GetMapping @Transactional(readOnly=true) public List<View> all(){return teams.findAll().stream().map(this::view).toList();}
 private void fill(Team t,Request r){
  if(r.name()==null || r.name().isBlank() || r.name().length()>150)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nom invalide");
  var d=departments.findById(r.departmentId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Département introuvable"));
  if(d.getStatus()!=com.ocp.organisation_service.enums.DepartmentStatus.ACTIVE)throw new ResponseStatusException(HttpStatus.CONFLICT,"Département archivé");
  if(r.responsableId()!=null && !users.existsById(r.responsableId()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Responsable introuvable");
  t.setName(r.name().trim());t.setDepartment(d);t.setResponsableId(r.responsableId());t.setObjective(r.objective());t.setActive(!Boolean.FALSE.equals(r.active()));
 }
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional public View create(@RequestBody Request r){Team t=new Team();t.setId(UUID.randomUUID());fill(t,r);teams.save(t);return view(t);}
 @PutMapping("/{id}") @Transactional public View update(@PathVariable UUID id,@RequestBody Request r){Team t=find(id);fill(t,r);return view(teams.save(t));}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Transactional public void delete(@PathVariable UUID id){Team t=find(id);members.deleteByTeamId(id);teams.delete(t);}
 @PutMapping("/{id}/members/{userId}") @Transactional public View add(@PathVariable UUID id,@PathVariable UUID userId){Team t=find(id);if(!users.existsById(userId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Collaborateur introuvable");if(!members.existsByTeamIdAndUserId(id,userId)){TeamMember m=new TeamMember();m.setTeamId(id);m.setUserId(userId);m.setRoleInTeam("MEMBER");m.setJoinedAt(LocalDateTime.now());members.save(m);}return view(t);}
 @DeleteMapping("/{id}/members/{userId}") @Transactional public View remove(@PathVariable UUID id,@PathVariable UUID userId){Team t=find(id);members.deleteByTeamIdAndUserId(id,userId);return view(t);}
}
