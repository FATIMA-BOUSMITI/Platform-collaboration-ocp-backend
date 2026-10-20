package com.ocp.project_management_service.service;
import com.ocp.project_management_service.entity.Task;
import com.ocp.project_management_service.repository.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import java.util.*;
import java.time.*;
import java.nio.charset.StandardCharsets;
@Service @EnableScheduling
public class CollaborationDispatcher {
 private final CollaborationOutboxRepository outbox;private final TaskRepository tasks;private final CollaborationEvents events;private final RestClient client;private final javax.crypto.SecretKey key;
 public CollaborationDispatcher(CollaborationOutboxRepository outbox,TaskRepository tasks,CollaborationEvents events,@Value("${collaboration.service.url:http://localhost:8089}")String url,@Value("${jwt.secret}")String secret){this.outbox=outbox;this.tasks=tasks;this.events=events;key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));var f=new org.springframework.http.client.SimpleClientHttpRequestFactory();f.setConnectTimeout(Duration.ofSeconds(3));f.setReadTimeout(Duration.ofSeconds(5));client=RestClient.builder().baseUrl(url).requestFactory(f).build();}
 @Scheduled(fixedDelayString="${collaboration.outbox.delay:5000}")public void deliver(){for(var row:outbox.findTop50ByDeliveredAtIsNullAndNextAttemptAtBeforeOrderByCreatedAtAsc(LocalDateTime.now())){try{String token=Jwts.builder().issuer("project-management-service").claim("type","COLLABORATION_SERVICE").claim("serviceAudience","communication-service").issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+30000)).signWith(key).compact();client.post().uri("/api/internal/collaboration/events").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).body(row.getPayload()).retrieve().toBodilessEntity();row.setDeliveredAt(LocalDateTime.now());}catch(RuntimeException failure){row.setAttempts(row.getAttempts()+1);row.setNextAttemptAt(LocalDateTime.now().plusSeconds(Math.min(3600,5L*(1L<<Math.min(row.getAttempts(),9)))));org.slf4j.LoggerFactory.getLogger(CollaborationDispatcher.class).warn("Collaboration event {} deferred (attempt {})",row.getId(),row.getAttempts());}outbox.save(row);}}
 @Scheduled(fixedDelayString="${collaboration.deadlines.delay:300000}")public void deadlines(){for(Task task:tasks.findByDueDateLessThanEqualAndStatusNotIn(LocalDate.now().plusDays(1),List.of(Task.TaskStatus.DONE,Task.TaskStatus.ARCHIVED)))events.deadline(task);}
}
