package com.ocp.project_management_service.service;
import com.ocp.project_management_service.entity.*;
import com.ocp.project_management_service.repository.*;
import com.ocp.project_management_service.mapper.TaskMapper;
import com.ocp.project_management_service.dto.response.TaskResponseDTO;
import com.ocp.project_management_service.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;
@Service @RequiredArgsConstructor
public class BoardService {
 private final BoardColumnRepository columns;private final ProjectRepository projects;private final TaskRepository tasks;private final ProjectAccessPolicy access;private final TaskMapper mapper;
 public record ColumnRequest(String name,Task.TaskStatus status,Integer position){}
 public record Move(UUID columnId,Integer position,Long expectedVersion){}
 private Project project(UUID id){return projects.lockById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Projet introuvable"));}
 private BoardColumn column(UUID project,UUID id){return columns.findById(id).filter(c->c.getProjectId().equals(project)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Colonne introuvable"));}
 @Transactional public List<BoardColumn> list(UUID id,AuthenticatedUser user){Project p=project(id);access.requireProjectRead(p,user);var existing=columns.findByProjectIdOrderByPositionAscIdAsc(id);if(existing.isEmpty()){String[] labels={"À faire","En cours","En revue","Terminé"};Task.TaskStatus[] statuses={Task.TaskStatus.TODO,Task.TaskStatus.IN_PROGRESS,Task.TaskStatus.REVIEW,Task.TaskStatus.DONE};for(int i=0;i<labels.length;i++){var c=new BoardColumn();c.setId(UUID.randomUUID());c.setProjectId(id);c.setName(labels[i]);c.setStatus(statuses[i]);c.setPosition(i);columns.save(c);}existing=columns.findByProjectIdOrderByPositionAscIdAsc(id);}return existing;}
 private void fill(BoardColumn c,ColumnRequest request){if(request.name()==null||request.name().isBlank()||request.name().length()>100||request.status()==null||request.status()==Task.TaskStatus.ARCHIVED)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nom ou statut de colonne invalide");c.setName(request.name().trim());c.setStatus(request.status());if(request.position()!=null)c.setPosition(Math.max(0,request.position()));}
 @Transactional public BoardColumn create(UUID id,ColumnRequest request,AuthenticatedUser user){access.requireProjectManagement(project(id),user);list(id,user);var c=new BoardColumn();c.setId(UUID.randomUUID());c.setProjectId(id);c.setPosition(columns.findByProjectIdOrderByPositionAscIdAsc(id).size());fill(c,request);return columns.save(c);}
 @Transactional public BoardColumn update(UUID projectId,UUID id,ColumnRequest request,AuthenticatedUser user){access.requireProjectManagement(project(projectId),user);var c=column(projectId,id);var old=c.getStatus();fill(c,request);if(old!=c.getStatus())for(Task t:tasks.findByProjectId(projectId))if(id.equals(t.getColumnId()))t.setStatus(c.getStatus());return columns.save(c);}
 @Transactional public void delete(UUID projectId,UUID id,AuthenticatedUser user){access.requireProjectManagement(project(projectId),user);var c=column(projectId,id);var all=columns.findByProjectIdOrderByPositionAscIdAsc(projectId);if(all.size()<=1)throw new ResponseStatusException(HttpStatus.CONFLICT,"Conservez au moins une colonne");if(tasks.findByProjectId(projectId).stream().anyMatch(t->t.getStatus()!=Task.TaskStatus.ARCHIVED&&(id.equals(t.getColumnId())||(t.getColumnId()==null&&t.getStatus()==c.getStatus()))))throw new ResponseStatusException(HttpStatus.CONFLICT,"Déplacez les tâches avant de supprimer la colonne");columns.delete(c);}
 @Transactional public TaskResponseDTO move(UUID taskId,Move request,AuthenticatedUser user){
  Task task=tasks.findById(taskId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Tâche introuvable"));project(task.getProject().getId());access.requireTaskStatusChange(task,user);
  if(request.columnId()==null||request.expectedVersion()==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Colonne et version requises");
  if(task.getVersion()!=request.expectedVersion())throw new ResponseStatusException(HttpStatus.CONFLICT,"La tâche a changé. Actualisez le tableau.");
  var c=column(task.getProject().getId(),request.columnId());
  var ordered=new ArrayList<>(tasks.findByProjectId(task.getProject().getId()).stream().filter(t->!t.getId().equals(taskId)&&(c.getId().equals(t.getColumnId())||(t.getColumnId()==null&&t.getStatus()==c.getStatus()))).sorted(Comparator.comparingLong(Task::getSortIndex).thenComparing(Task::getId)).toList());
  int index=request.position()==null?ordered.size():Math.max(0,Math.min(request.position(),ordered.size()));ordered.add(index,task);task.setColumnId(c.getId());task.setStatus(c.getStatus());for(int i=0;i<ordered.size();i++)ordered.get(i).setSortIndex(i*1000L);tasks.saveAll(ordered);tasks.flush();return mapper.toResponseDTO(task);
 }
 public void assignInitialColumn(Task task,UUID columnId,AuthenticatedUser user){var available=list(task.getProject().getId(),user);BoardColumn c=columnId==null?available.get(0):column(task.getProject().getId(),columnId);task.setColumnId(c.getId());task.setStatus(c.getStatus());task.setSortIndex(tasks.findByProjectId(task.getProject().getId()).stream().mapToLong(Task::getSortIndex).max().orElse(0)+1000);}
}
