package com.ocp.project_management_service.controller;

import com.ocp.project_management_service.dto.request.CommentRequestDTO;
import com.ocp.project_management_service.dto.request.SubTaskRequestDTO;
import com.ocp.project_management_service.dto.request.TaskRequestDTO;
import com.ocp.project_management_service.dto.response.CommentDTO;
import com.ocp.project_management_service.dto.response.SubTaskDTO;
import com.ocp.project_management_service.dto.response.TaskResponseDTO;
import com.ocp.project_management_service.entity.Task;
import com.ocp.project_management_service.security.AuthenticatedUser;
import com.ocp.project_management_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    public record StatusRequest(Task.TaskStatus status){}


    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponseDTO> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody TaskRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        TaskResponseDTO response =
                taskService.createTask(projectId, request, authenticatedUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }



    /*@GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByProject(
            @PathVariable UUID projectId) {

        List<TaskResponseDTO> response =
                taskService.getTasksByProject(projectId);

        return ResponseEntity.ok(response);
    }*/


    // GET /api/projects/{projectId}/tasks?status=TODO
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByProject(
            @PathVariable UUID projectId,
            @RequestParam(required = false) Task.TaskStatus status,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        List<TaskResponseDTO> response;

        if (status != null) {
            response = taskService.getTasksByProjectAndStatus(
                    projectId, status, authenticatedUser);
        } else {
            response = taskService.getTasksByProject(projectId, authenticatedUser);
        }

        return ResponseEntity.ok(response);
    }


    @GetMapping("/tasks/{id}/mention-candidates")
    public java.util.Set<UUID> mentionCandidates(@PathVariable UUID id,@AuthenticationPrincipal AuthenticatedUser user){return taskService.mentionCandidates(id,user);}
    // GET /api/tasks/{id}
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        TaskResponseDTO response =
                taskService.getTaskById(id, authenticatedUser);

        return ResponseEntity.ok(response);
    }


    // GET /api/tasks/assignee/{userId}
    @GetMapping("/tasks/assignee/{userId}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByAssignee(
            @PathVariable UUID userId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        List<TaskResponseDTO> response =
                taskService.getTasksByAssignee(userId, authenticatedUser);

        return ResponseEntity.ok(response);
    }


    // PUT /api/tasks/{id}
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody TaskRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        TaskResponseDTO response =
                taskService.updateTask(id, request, authenticatedUser);

        return ResponseEntity.ok(response);
    }


    // PATCH /api/tasks/{id}/status
    @PatchMapping("/tasks/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable UUID id,
            @RequestBody StatusRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        TaskResponseDTO response =
                taskService.updateTaskStatus(id, request.status(), authenticatedUser);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<SubTaskDTO> addSubTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody SubTaskRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        SubTaskDTO response =
                taskService.addSubTask(taskId, request, authenticatedUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // GET /api/tasks/{taskId}/subtasks
    @GetMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<List<SubTaskDTO>> getSubTasks(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        List<SubTaskDTO> response =
                taskService.getSubTasksByTask(taskId, authenticatedUser);

        return ResponseEntity.ok(response);
    }


    // PATCH /api/subtasks/{id}/toggle
    @PatchMapping("/subtasks/{id}/toggle")
    public ResponseEntity<SubTaskDTO> toggleSubTask(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        SubTaskDTO response =
                taskService.toggleSubTaskDone(id, authenticatedUser);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable UUID taskId,
            @Valid @RequestBody CommentRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        CommentDTO response =
                taskService.addComment(taskId, request, authenticatedUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }



    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

        List<CommentDTO> response =
                taskService.getCommentsByTask(taskId, authenticatedUser);

        return ResponseEntity.ok(response);
    }
}
