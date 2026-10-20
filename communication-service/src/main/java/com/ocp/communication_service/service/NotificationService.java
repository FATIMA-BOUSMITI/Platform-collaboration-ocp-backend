package com.ocp.communication_service.service;
import com.ocp.communication_service.entity.*;
import com.ocp.communication_service.repository.*;
import com.ocp.communication_service.realtime.RealtimeEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.PageRequest;
import java.util.*;
import java.time.LocalDateTime;
@Service @RequiredArgsConstructor
public class NotificationService {
 private final NotificationRepository notifications;private final NotificationPreferenceRepository preferences;private final RealtimeEvents events;
 public record Event(UUID id,UUID userId,String type,String title,String body,String link){}
 @Transactional public CollaborationNotification publish(Event event){
  if(event.id()==null||event.userId()==null||event.title()==null||event.title().isBlank()||event.title().length()>150||event.body()==null||event.body().length()>500||event.link()==null||event.link().length()>500||!event.link().matches("^/(projects|messages)(\\?.*)?$"))throw new IllegalArgumentException("Notification invalide");
  if(!Set.of("MESSAGE","TASK_ASSIGNED","TASK_UPDATED","TASK_STATUS","TASK_COMMENT","TASK_DUE").contains(event.type()))throw new IllegalArgumentException("Événement inconnu");
  var existing=notifications.findByExternalId(event.id());if(existing.isPresent())return existing.get();
  var pref=preferences(event.userId());if((event.type().equals("MESSAGE")&&!pref.isMessages())||(event.type().equals("TASK_DUE")&&!pref.isDeadlines())||(!event.type().equals("MESSAGE")&&!event.type().equals("TASK_DUE")&&!pref.isTasks()))return null;
  var n=new CollaborationNotification();n.setId(UUID.randomUUID());n.setExternalId(event.id());n.setUserId(event.userId());n.setType(event.type());n.setTitle(event.title());n.setBody(event.body());n.setLink(event.link());n.setCreatedAt(LocalDateTime.now());notifications.save(n);events.send(List.of(event.userId()),"notifications",Map.of("kind","notification","notification",n));return n;
 }
 @Transactional(readOnly=true) public List<CollaborationNotification> list(UUID user,boolean unread,int page){return unread?notifications.findByUserIdAndReadFalseOrderByCreatedAtDesc(user,PageRequest.of(Math.max(0,page),40)):notifications.findByUserIdOrderByCreatedAtDesc(user,PageRequest.of(Math.max(0,page),40));}
 @Transactional(readOnly=true) public long unread(UUID user){return notifications.countByUserIdAndReadFalse(user);}
 @Transactional public void read(UUID id,UUID user,boolean read){var n=notifications.findById(id).filter(v->v.getUserId().equals(user)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Notification introuvable"));n.setRead(read);notifications.save(n);events.send(List.of(user),"notifications",Map.of("kind","notification-state"));}
 @Transactional public void readAll(UUID user){notifications.readAll(user);events.send(List.of(user),"notifications",Map.of("kind","notification-state"));}
 @Transactional(readOnly=true) public NotificationPreference preferences(UUID user){return preferences.findById(user).orElseGet(()->{var p=new NotificationPreference();p.setUserId(user);return p;});}
 @Transactional public NotificationPreference preferences(UUID user,NotificationPreference value){value.setUserId(user);return preferences.save(value);}
}
