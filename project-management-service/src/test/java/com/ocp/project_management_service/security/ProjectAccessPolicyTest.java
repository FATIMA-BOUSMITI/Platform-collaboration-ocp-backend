package com.ocp.project_management_service.security;

import com.ocp.project_management_service.entity.Project;
import com.ocp.project_management_service.entity.Task;
import com.ocp.project_management_service.repository.TaskRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.access.AccessDeniedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectAccessPolicyTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectAccessPolicy policy;

    @Test
    void administratorAndDirectorCanReadAndManageAnyProject() {
        Project project = project(UUID.randomUUID(), UUID.randomUUID());

        assertTrue(policy.canReadProject(project, user("ADMIN", UUID.randomUUID())));
        assertTrue(policy.canManageProject(project, user("DIRECTOR", UUID.randomUUID())));
        verifyNoInteractions(taskRepository);
    }

    @Test
    void managerCanManageOnlyResponsibleProjects() {
        UUID managerId = UUID.randomUUID();
        Project ownedProject = project(UUID.randomUUID(), managerId);
        Project otherProject = project(UUID.randomUUID(), UUID.randomUUID());

        assertTrue(policy.canManageProject(ownedProject, user("MANAGER", managerId)));
        assertFalse(policy.canManageProject(otherProject, user("MANAGER", managerId)));
    }

    @Test
    void employeeCanReadAndWorkOnlyOnAssignedTasks() {
        UUID employeeId = UUID.randomUUID();
        Project project = project(UUID.randomUUID(), UUID.randomUUID());
        Task assignedTask = Task.builder()
            .project(project)
            .assigneeId(employeeId)
            .build();
        Task otherTask = Task.builder()
            .project(project)
            .assigneeId(UUID.randomUUID())
            .build();
        when(taskRepository.existsByProjectIdAndAssigneeId(project.getId(), employeeId))
            .thenReturn(true);

        assertTrue(policy.canReadProject(project, user("EMPLOYEE", employeeId)));
        assertTrue(policy.canReadTask(assignedTask, user("EMPLOYEE", employeeId)));
        assertFalse(policy.canReadTask(otherTask, user("EMPLOYEE", employeeId)));
        assertThrows(AccessDeniedException.class,
            () -> policy.requireProjectManagement(project, user("EMPLOYEE", employeeId)));
        verify(taskRepository).existsByProjectIdAndAssigneeId(project.getId(), employeeId);
    }

    @Test
    void employeeProjectListingUsesAssignedTaskProjects() {
        UUID employeeId = UUID.randomUUID();
        Project assignedProject = project(UUID.randomUUID(), UUID.randomUUID());
        Project unrelatedProject = project(UUID.randomUUID(), UUID.randomUUID());
        when(taskRepository.findDistinctProjectIdsByAssigneeId(employeeId))
            .thenReturn(List.of(assignedProject.getId()));

        assertTrue(policy.readableProjects(
            List.of(assignedProject, unrelatedProject), user("EMPLOYEE", employeeId))
            .contains(assignedProject));
        assertFalse(policy.readableProjects(
            List.of(assignedProject, unrelatedProject), user("EMPLOYEE", employeeId))
            .contains(unrelatedProject));
    }

    private Project project(UUID id, UUID responsibleId) {
        return Project.builder().id(id).responsibleId(responsibleId).build();
    }

    private AuthenticatedUser user(String role, UUID userId) {
        return new AuthenticatedUser("qa-user@example.test", userId, role);
    }
}
