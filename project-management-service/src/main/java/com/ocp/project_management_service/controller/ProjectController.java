package com.ocp.project_management_service.controller;

import com.ocp.project_management_service.dto.request.ProjectRequestDTO;
import com.ocp.project_management_service.dto.request.ProjectUpdateRequestDTO;
import com.ocp.project_management_service.dto.response.ProjectResponseDTO;
import com.ocp.project_management_service.entity.Project;
import com.ocp.project_management_service.security.AuthenticatedUser;
import com.ocp.project_management_service.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/projects")
@RestController
@AllArgsConstructor
public class ProjectController {
    private final ProjectService projectService ;

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(
            @Valid @RequestBody ProjectRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        ProjectResponseDTO response = projectService.createProject(request, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        List<ProjectResponseDTO> response = projectService.getAllProjects(authenticatedUser);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        ProjectResponseDTO response = projectService.getProjectById(id, authenticatedUser);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(
            @PathVariable UUID deptId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        List<ProjectResponseDTO> response =
                projectService.getProjectsByDepartment(deptId, authenticatedUser);
        return ResponseEntity.ok().body(response);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable UUID id ,
                                                            @Valid @RequestBody
                                                            ProjectUpdateRequestDTO request,
                                                            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        ProjectResponseDTO response =
                projectService.updateProject(id, request, authenticatedUser);
        return ResponseEntity.ok(response);

    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponseDTO> updateStatus(@PathVariable UUID id ,
                                                            @Valid @RequestBody
                                                            Project.ProjectStatus status,
                                                            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        ProjectResponseDTO response =
                projectService.updateStatus(id, status, authenticatedUser);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        projectService.archiveProject(id, authenticatedUser);

        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        projectService.deleteProject(id, authenticatedUser);

        return ResponseEntity.noContent().build();

    }


}
