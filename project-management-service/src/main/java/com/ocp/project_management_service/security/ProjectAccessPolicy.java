package com.ocp.project_management_service.security;

import com.ocp.project_management_service.entity.Project;
import com.ocp.project_management_service.entity.Task;
import com.ocp.project_management_service.repository.TaskRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class ProjectAccessPolicy {

    private final TaskRepository taskRepository;

    public ProjectAccessPolicy(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean canReadProject(Project project, AuthenticatedUser user) {
        if (user == null) {
            return true;
        }
        if (user.isAdministratorOrDirector()) {
            return true;
        }
        if (user.isManager()) {
            return user.userId().equals(project.getResponsibleId());
        }
        return user.isEmployee()
            && taskRepository.existsByProjectIdAndAssigneeId(project.getId(), user.userId());
    }

    public List<Project> readableProjects(
            Collection<Project> projects, AuthenticatedUser user) {
        if (user == null) {
            return List.copyOf(projects);
        }
        if (user.isAdministratorOrDirector()) {
            return List.copyOf(projects);
        }
        if (user.isManager()) {
            return projects.stream()
                .filter(project -> user.userId().equals(project.getResponsibleId()))
                .toList();
        }
        if (!user.isEmployee()) {
            return List.of();
        }
        Set<UUID> assignedProjectIds = taskRepository
            .findDistinctProjectIdsByAssigneeId(user.userId())
            .stream()
            .collect(Collectors.toSet());
        return projects.stream()
            .filter(project -> assignedProjectIds.contains(project.getId()))
            .toList();
    }

    public boolean canManageProject(Project project, AuthenticatedUser user) {
        if (user == null) {
            return true;
        }
        return user.isAdministratorOrDirector()
            || (user.isManager() && user.userId().equals(project.getResponsibleId()));
    }

    public void requireProjectRead(Project project, AuthenticatedUser user) {
        if (!canReadProject(project, user)) {
            throw new AccessDeniedException("Project access is not permitted");
        }
    }

    public void requireProjectManagement(Project project, AuthenticatedUser user) {
        if (!canManageProject(project, user)) {
            throw new AccessDeniedException("Project management is not permitted");
        }
    }

    public void requireProjectAssignment(UUID responsibleId, AuthenticatedUser user) {
        if (user == null) {
            return;
        }
        if (!user.isAdministratorOrDirector()
            && (!user.isManager() || !user.userId().equals(responsibleId))) {
            throw new AccessDeniedException("Only administrators, directors, or the responsible manager can assign a project");
        }
    }

    public boolean canReadTask(Task task, AuthenticatedUser user) {
        if (user == null) {
            return true;
        }
        return canManageProject(task.getProject(), user)
            || (user.isEmployee() && user.userId().equals(task.getAssigneeId()));
    }

    public void requireTaskRead(Task task, AuthenticatedUser user) {
        if (!canReadTask(task, user)) {
            throw new AccessDeniedException("Task access is not permitted");
        }
    }

    public void requireTaskManagement(Task task, AuthenticatedUser user) {
        requireProjectManagement(task.getProject(), user);
    }

    public void requireTaskWork(Task task, AuthenticatedUser user) {
        if (!canReadTask(task, user)) {
            throw new AccessDeniedException("Task work is not permitted");
        }
    }

    public void requireTaskStatusChange(Task task, AuthenticatedUser user) {
        requireTaskWork(task, user);
    }
}
