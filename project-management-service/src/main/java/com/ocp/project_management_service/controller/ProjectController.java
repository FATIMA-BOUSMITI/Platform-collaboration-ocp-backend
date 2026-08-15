package com.ocp.project_management_service.controller;

import com.ocp.project_management_service.dto.request.ProjectRequestDTO;
import com.ocp.project_management_service.dto.request.ProjectUpdateRequestDTO;
import com.ocp.project_management_service.dto.response.ProjectResponseDTO;
import com.ocp.project_management_service.entity.Project;
import com.ocp.project_management_service.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody ProjectRequestDTO request){
        ProjectResponseDTO response = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(){
        List<ProjectResponseDTO> response = projectService.getAllProjects();
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProject(@PathVariable UUID id){
        ProjectResponseDTO response = projectService.getProjectById(id);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(@PathVariable UUID deptId){
        List<ProjectResponseDTO> response = projectService.getProjectsByDepartment(deptId);
        return ResponseEntity.ok().body(response);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable UUID id ,
                                                            @Valid @RequestBody
                                                            ProjectUpdateRequestDTO request){

        ProjectResponseDTO response = projectService.updateProject(id,request);
        return ResponseEntity.ok(response);

    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponseDTO> updateStatus(@PathVariable UUID id ,
                                                            @Valid @RequestBody
                                                            Project.ProjectStatus status){

        ProjectResponseDTO response = projectService.updateStatus(id,status);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveProject(@PathVariable UUID id ){

        projectService.archiveProject(id);

        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id ){

        projectService.deleteProject(id);

        return ResponseEntity.noContent().build();

    }


}
