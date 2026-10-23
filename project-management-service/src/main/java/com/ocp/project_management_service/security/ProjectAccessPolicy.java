package com.ocp.project_management_service.security;
import com.ocp.project_management_service.entity.*;
import com.ocp.project_management_service.repository.*;
import java.util.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
@Component public class ProjectAccessPolicy {
 private final TaskRepository taskRepository;
 @org.springframework.beans.factory.annotation.Autowired private ProjectInvitationRepository invitations;
 public ProjectAccessPolicy(TaskRepository tasks){this.taskRepository=tasks;}
 public boolean isInvited(UUID project,UUID user){return invitations!=null&&invitations.existsByProjectIdAndUserIdAndStatus(project,user,ProjectInvitation.Status.ACCEPTED);}
 public boolean canReadProject(Project project,AuthenticatedUser user){if(user==null||user.isAdministratorOrDirector())return true;if(isInvited(project.getId(),user.userId()))return true;if(user.isManager())return user.userId().equals(project.getResponsibleId());return user.isEmployee()&&taskRepository.existsByProjectIdAndAssigneeId(project.getId(),user.userId());}
 public List<Project> readableProjects(Collection<Project> projects,AuthenticatedUser user){
  if(user!=null&&user.isEmployee()){
   Set<UUID> assignedProjectIds=new HashSet<>(taskRepository.findDistinctProjectIdsByAssigneeId(user.userId()));
   return projects.stream().filter(p->assignedProjectIds.contains(p.getId())||isInvited(p.getId(),user.userId())).toList();
  }
  return projects.stream().filter(p->canReadProject(p,user)).toList();
 }
 public boolean canManageProject(Project project,AuthenticatedUser user){return user==null||user.isAdministratorOrDirector()||(user.isManager()&&user.userId().equals(project.getResponsibleId()));}
 public void requireProjectRead(Project project,AuthenticatedUser user){if(!canReadProject(project,user))throw new AccessDeniedException("Project access is not permitted");}
 public void requireProjectManagement(Project project,AuthenticatedUser user){if(!canManageProject(project,user))throw new AccessDeniedException("Project management is not permitted");}
 public void requireProjectAssignment(UUID responsibleId,AuthenticatedUser user){if(user!=null&&!user.isAdministratorOrDirector()&&(!user.isManager()||!user.userId().equals(responsibleId)))throw new AccessDeniedException("Only administrators, directors, or the responsible manager can assign a project");}
 public boolean canReadTask(Task task,AuthenticatedUser user){return user==null||canManageProject(task.getProject(),user)||isInvited(task.getProject().getId(),user.userId())||(user.isEmployee()&&user.userId().equals(task.getAssigneeId()));}
 public void requireTaskRead(Task task,AuthenticatedUser user){if(!canReadTask(task,user))throw new AccessDeniedException("Task access is not permitted");}
 public void requireTaskManagement(Task task,AuthenticatedUser user){requireProjectManagement(task.getProject(),user);}
 public void requireTaskWork(Task task,AuthenticatedUser user){requireTaskRead(task,user);}
 public void requireTaskStatusChange(Task task,AuthenticatedUser user){if(user!=null&&!canManageProject(task.getProject(),user)&&!user.userId().equals(task.getAssigneeId()))throw new AccessDeniedException("Only the assignee or project manager can change this task");requireTaskWork(task,user);}
}
