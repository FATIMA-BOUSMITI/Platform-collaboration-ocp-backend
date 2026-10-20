package com.ocp.project_management_service.service;
import com.ocp.project_management_service.entity.*;
import com.ocp.project_management_service.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.LocalDateTime;
@Service @RequiredArgsConstructor
public class CollaborationEvents {
 private final CollaborationOutboxRepository outbox;private final ObjectMapper json;
 public record Event(UUID id,UUID userId,String type,String title,String body,String link){}
 @Transactional public void task(Task task,String type,UUID actor){Set<UUID> recipients=new HashSet<>();if(task.getAssigneeId()!=null)recipients.add(task.getAssigneeId());if(task.getProject().getResponsibleId()!=null)recipients.add(task.getProject().getResponsibleId());if(actor!=null)recipients.remove(actor);for(UUID user:recipients)enqueue(user,type,title(type),task.getTitle(),"/projects?project="+task.getProject().getId()+"&task="+task.getId(),UUID.randomUUID().toString());}
 @Transactional public void deadline(Task task){if(task.getAssigneeId()!=null)enqueue(task.getAssigneeId(),"TASK_DUE","Échéance de tâche",task.getTitle()+" · "+task.getDueDate(),"/projects?project="+task.getProject().getId()+"&task="+task.getId(),"due:"+task.getId()+":"+task.getDueDate()+":"+task.getAssigneeId());}
 private String title(String type){return switch(type){case "TASK_ASSIGNED"->"Tâche attribuée";case "TASK_COMMENT"->"Nouveau commentaire";case "TASK_STATUS"->"Statut de tâche modifié";default->"Tâche mise à jour";};}
 private void enqueue(UUID user,String type,String title,String body,String link,String dedup){if(outbox.existsByDedupKey(dedup))return;var row=new CollaborationOutbox();row.setId(UUID.randomUUID());row.setDedupKey(dedup);row.setCreatedAt(LocalDateTime.now());row.setNextAttemptAt(LocalDateTime.now());try{row.setPayload(json.writeValueAsString(new Event(row.getId(),user,type,title,body.substring(0,Math.min(500,body.length())),link)));}catch(com.fasterxml.jackson.core.JsonProcessingException error){throw new IllegalStateException("Événement non sérialisable",error);}outbox.save(row);}
}
