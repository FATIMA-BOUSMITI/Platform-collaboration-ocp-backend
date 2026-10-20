package com.ocp.communication_service.controller;
import com.ocp.communication_service.service.NotificationService;
import com.ocp.communication_service.entity.*;
import com.ocp.communication_service.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.*;
@RestController @RequestMapping("/api/notifications") @RequiredArgsConstructor
public class NotificationController {
 private final NotificationService service;
 private UUID id(AuthenticatedUser user){if(user==null)throw new org.springframework.security.access.AccessDeniedException("Authentification requise");return user.userId();}
 public record Read(boolean read){}
 @GetMapping public List<CollaborationNotification> list(@AuthenticationPrincipal AuthenticatedUser user,@RequestParam(defaultValue="false")boolean unread,@RequestParam(defaultValue="0")int page){return service.list(id(user),unread,page);}
 @GetMapping("/unread-count")public Map<String,Long> count(@AuthenticationPrincipal AuthenticatedUser user){return Map.of("count",service.unread(id(user)));}
 @PatchMapping("/{notificationId}/read")public void read(@PathVariable UUID notificationId,@RequestBody Read request,@AuthenticationPrincipal AuthenticatedUser user){service.read(notificationId,id(user),request.read());}
 @PatchMapping("/read-all")public void readAll(@AuthenticationPrincipal AuthenticatedUser user){service.readAll(id(user));}
 @GetMapping("/preferences")public NotificationPreference preferences(@AuthenticationPrincipal AuthenticatedUser user){return service.preferences(id(user));}
 @PutMapping("/preferences")public NotificationPreference preferences(@RequestBody NotificationPreference value,@AuthenticationPrincipal AuthenticatedUser user){return service.preferences(id(user),value);}
}
