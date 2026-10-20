package com.ocp.communication_service.service;
import com.ocp.communication_service.entity.*;
import com.ocp.communication_service.repository.*;
import com.ocp.communication_service.realtime.RealtimeEvents;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.*;
import java.util.*;
import java.nio.file.*;
@Service
public class AttachmentService {
 private final MessageAttachmentRepository attachments;private final MessageRepository messages;private final ConversationMemberRepository members;private final RealtimeEvents events;private final Path root;
 public AttachmentService(MessageAttachmentRepository attachments,MessageRepository messages,ConversationMemberRepository members,RealtimeEvents events,@Value("${collaboration.storage.path:${java.io.tmpdir}/ocp-collaboration}")String folder){this.attachments=attachments;this.messages=messages;this.members=members;this.events=events;root=Path.of(folder).toAbsolutePath().normalize();}
 public record View(UUID id,String name,String contentType,long size){}
 public List<View> list(UUID messageId){return attachments.findByMessageId(messageId).stream().map(a->new View(a.getId(),a.getOriginalName(),a.getContentType(),a.getSize())).toList();}
 private void member(UUID id,UUID user){if(user==null||!members.existsByConversationIdAndUserId(id,user))throw new AccessDeniedException("Conversation non accessible");}
 @Transactional public View upload(UUID conversationId,UUID messageId,MultipartFile file,UUID user)throws java.io.IOException{
  member(conversationId,user);var message=messages.findById(messageId).filter(m->m.getConversationId().equals(conversationId)&&m.getSenderId().equals(user)).orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN,"Seul l’auteur peut joindre un fichier"));
  if(file.isEmpty()||file.getSize()>10*1024*1024)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Le fichier doit faire entre 1 octet et 10 Mo");
  String type=Optional.ofNullable(file.getContentType()).orElse("application/octet-stream");
  if(!Set.of("application/pdf","image/png","image/jpeg","image/webp","text/plain","text/csv","application/zip","application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").contains(type))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ce type de fichier n’est pas autorisé");
  if(attachments.findByMessageId(messageId).size()>=5)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cinq pièces jointes maximum par message");
  var a=new MessageAttachment();a.setId(UUID.randomUUID());a.setMessageId(messageId);a.setConversationId(conversationId);a.setStoredName(a.getId().toString());String name=Optional.ofNullable(file.getOriginalFilename()).orElse("fichier").replaceAll("[\\\\/\\r\\n]","_");a.setOriginalName(name.substring(0,Math.min(200,name.length())));a.setContentType(type);a.setSize(file.getSize());Files.createDirectories(root);Path target=root.resolve(a.getStoredName());try(var in=file.getInputStream()){Files.copy(in,target);}org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(new org.springframework.transaction.support.TransactionSynchronization(){@Override public void afterCompletion(int status){if(status!=STATUS_COMMITTED)try{Files.deleteIfExists(target);}catch(java.io.IOException ignored){}}});attachments.save(a);events.send(members.findByConversationId(conversationId).stream().map(ConversationMember::getUserId).toList(),"messages",Map.of("kind","attachment","conversationId",conversationId,"messageId",message.getId()));return new View(a.getId(),a.getOriginalName(),a.getContentType(),a.getSize());
 }
 @Transactional(readOnly=true)public MessageAttachment metadata(UUID conversationId,UUID id,UUID user){member(conversationId,user);return attachments.findById(id).filter(a->a.getConversationId().equals(conversationId)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Fichier introuvable"));}
 public Resource resource(MessageAttachment a){Path target=root.resolve(a.getStoredName()).normalize();if(!target.startsWith(root)||!Files.isRegularFile(target))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Fichier indisponible");return new FileSystemResource(target);}
}
