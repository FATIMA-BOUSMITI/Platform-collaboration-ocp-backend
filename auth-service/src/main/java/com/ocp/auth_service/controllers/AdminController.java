package com.ocp.auth_service.controllers;
import com.ocp.auth_service.services.AdminService;
import com.ocp.auth_service.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.*;
@RestController @RequestMapping("/api/auth/admin") @RequiredArgsConstructor
public class AdminController {
 private final AdminService service;
 @GetMapping("/stats") public Map<String,Long> stats(){return service.stats();}
 @GetMapping("/activity") public List<AdminService.Activity> activity(){return service.activity();}
 @GetMapping("/sessions") public List<AdminService.SessionView> sessions(@AuthenticationPrincipal AuthenticatedUser u){return service.sessions(u.email(),true);}
 @DeleteMapping("/sessions/{id}") public void revoke(@PathVariable UUID id,@AuthenticationPrincipal AuthenticatedUser u){service.revoke(id,u.email(),true);}
 @GetMapping("/settings") public Map<String,String> settings(@AuthenticationPrincipal AuthenticatedUser u){return service.settings(u.email());}
 @PutMapping("/settings") public Map<String,String> settings(@AuthenticationPrincipal AuthenticatedUser u,@RequestBody Map<String,String> value){return service.settings(u.email(),value);}
 @GetMapping("/notifications") public List<AdminService.NoticeView> notifications(@AuthenticationPrincipal AuthenticatedUser u){return service.notifications(u.email());}
 @PatchMapping("/notifications/{id}/read") public void read(@PathVariable UUID id,@AuthenticationPrincipal AuthenticatedUser u){service.read(id,u.email());}
}
