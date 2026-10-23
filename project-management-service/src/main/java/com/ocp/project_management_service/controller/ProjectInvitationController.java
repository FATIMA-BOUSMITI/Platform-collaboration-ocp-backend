package com.ocp.project_management_service.controller;
import com.ocp.project_management_service.security.AuthenticatedUser;
import com.ocp.project_management_service.service.ProjectInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.*;
@RestController @RequestMapping("/api") @RequiredArgsConstructor public class ProjectInvitationController {
 private final ProjectInvitationService service;
 public record Invite(@jakarta.validation.constraints.NotNull UUID userId){}
 public record Response(boolean accept){}
 @GetMapping("/project-invitations")public List<ProjectInvitationService.View> own(@AuthenticationPrincipal AuthenticatedUser user){return service.own(user);}
 @PatchMapping("/project-invitations/{id}")public ProjectInvitationService.View respond(@PathVariable UUID id,@RequestBody Response response,@AuthenticationPrincipal AuthenticatedUser user){return service.respond(id,response.accept(),user);}
 @GetMapping("/projects/{projectId}/invitations")public List<ProjectInvitationService.View> project(@PathVariable UUID projectId,@AuthenticationPrincipal AuthenticatedUser user){return service.project(projectId,user);}
 @PostMapping("/projects/{projectId}/invitations") @ResponseStatus(org.springframework.http.HttpStatus.CREATED)public ProjectInvitationService.View invite(@PathVariable UUID projectId,@jakarta.validation.Valid @RequestBody Invite request,@AuthenticationPrincipal AuthenticatedUser user){return service.invite(projectId,request.userId(),user);}
 @DeleteMapping("/projects/{projectId}/invitations/{id}") @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)public void cancel(@PathVariable UUID projectId,@PathVariable UUID id,@AuthenticationPrincipal AuthenticatedUser user){service.cancel(projectId,id,user);}
}
