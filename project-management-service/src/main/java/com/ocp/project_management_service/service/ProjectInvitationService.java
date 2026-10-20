package com.ocp.project_management_service.service;
import com.ocp.project_management_service.entity.*;
import com.ocp.project_management_service.repository.*;
import com.ocp.project_management_service.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.*;
@Service @RequiredArgsConstructor public class ProjectInvitationService {
 private final ProjectInvitationRepository invitations;private final ProjectRepository projects;private final ProjectAccessPolicy access;private final CollaborationDirectory directory;private final CollaborationEvents events;
 public record View(UUID id,UUID projectId,String projectName,UUID userId,UUID invitedBy,ProjectInvitation.Status status,LocalDateTime createdAt){}
 private UUID actor(AuthenticatedUser user){if(user==null)throw new org.springframework.security.access.AccessDeniedException("Authentification requise");return user.userId();}
 private View view(ProjectInvitation i){return new View(i.getId(),i.getProjectId(),projects.findById(i.getProjectId()).map(Project::getName).orElse("Projet indisponible"),i.getUserId(),i.getInvitedBy(),i.getStatus(),i.getCreatedAt());}
 @Transactional public View invite(UUID projectId,UUID userId,AuthenticatedUser user){UUID actor=actor(user);Project project=projects.lockById(projectId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Projet introuvable"));access.requireProjectManagement(project,user);if(userId==null||userId.equals(actor)||userId.equals(project.getResponsibleId()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choisissez un autre collaborateur");directory.requireUser(userId);
  var existing=invitations.findByProjectIdOrderByCreatedAtDesc(projectId).stream().filter(i->i.getUserId().equals(userId)&&(i.getStatus()==ProjectInvitation.Status.PENDING||i.getStatus()==ProjectInvitation.Status.ACCEPTED)).findFirst();if(existing.isPresent())return view(existing.get());
  var i=new ProjectInvitation();i.setId(UUID.randomUUID());i.setProjectId(projectId);i.setUserId(userId);i.setInvitedBy(actor);i.setStatus(ProjectInvitation.Status.PENDING);i.setCreatedAt(LocalDateTime.now());invitations.save(i);events.invitation(project,i);return view(i);
 }
 @Transactional(readOnly=true)public List<View> own(AuthenticatedUser user){return invitations.findByUserIdAndStatusOrderByCreatedAtDesc(actor(user),ProjectInvitation.Status.PENDING).stream().map(this::view).toList();}
 @Transactional(readOnly=true)public List<View> project(UUID id,AuthenticatedUser user){actor(user);var p=projects.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));access.requireProjectManagement(p,user);return invitations.findByProjectIdOrderByCreatedAtDesc(id).stream().map(this::view).toList();}
 @Transactional public View respond(UUID id,boolean accept,AuthenticatedUser user){UUID actor=actor(user);var i=invitations.findById(id).filter(v->v.getUserId().equals(actor)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Invitation introuvable"));projects.lockById(i.getProjectId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Projet introuvable"));if(i.getStatus()!=ProjectInvitation.Status.PENDING)throw new ResponseStatusException(HttpStatus.CONFLICT,"Invitation déjà traitée");i.setStatus(accept?ProjectInvitation.Status.ACCEPTED:ProjectInvitation.Status.DECLINED);i.setRespondedAt(LocalDateTime.now());invitations.save(i);return view(i);}
 @Transactional public void cancel(UUID projectId,UUID id,AuthenticatedUser user){actor(user);var p=projects.lockById(projectId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));access.requireProjectManagement(p,user);var i=invitations.findById(id).filter(v->v.getProjectId().equals(projectId)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(i.getStatus()!=ProjectInvitation.Status.PENDING)throw new ResponseStatusException(HttpStatus.CONFLICT,"Seule une invitation en attente peut être annulée");i.setStatus(ProjectInvitation.Status.CANCELLED);i.setRespondedAt(LocalDateTime.now());invitations.save(i);}
}
