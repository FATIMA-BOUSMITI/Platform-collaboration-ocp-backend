package com.ocp.project_management_service.controller;

import com.ocp.project_management_service.dto.request.CommentRequestDTO;
import com.ocp.project_management_service.dto.request.SubTaskRequestDTO;
import com.ocp.project_management_service.dto.request.TaskRequestDTO;
import com.ocp.project_management_service.dto.response.CommentDTO;
import com.ocp.project_management_service.dto.response.SubTaskDTO;
import com.ocp.project_management_service.dto.response.TaskResponseDTO;
import com.ocp.project_management_service.entity.Task;
import com.ocp.project_management_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;


    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponseDTO> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody TaskRequestDTO request) {

        TaskResponseDTO response =
                taskService.createTask(projectId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }



    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByProject(
            @PathVariable UUID projectId) {

        List<TaskResponseDTO> response =
                taskService.getTasksByProject(projectId);

        return ResponseEntity.ok(response);
    }


    // GET /api/projects/{projectId}/tasks?status=TODO
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByProjectAndStatus(
            @PathVariable UUID projectId,
            @RequestParam(required = false) Task.TaskStatus status) {

        if (status == null) {
            return ResponseEntity.ok(
                    taskService.getTasksByProject(projectId)
            );
        }

        return ResponseEntity.ok(
                taskService.getTasksByProjectAndStatus(projectId, status)
        );
    }


    // GET /api/tasks/{id}
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable UUID id) {

        TaskResponseDTO response =
                taskService.getTaskById(id);

        return ResponseEntity.ok(response);
    }


    // GET /api/tasks/assignee/{userId}
    @GetMapping("/tasks/assignee/{userId}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByAssignee(
            @PathVariable UUID userId) {

        List<TaskResponseDTO> response =
                taskService.getTasksByAssignee(userId);

        return ResponseEntity.ok(response);
    }


    // PUT /api/tasks/{id}
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody TaskRequestDTO request) {

        TaskResponseDTO response =
                taskService.updateTask(id, request);

        return ResponseEntity.ok(response);
    }


    // PATCH /api/tasks/{id}/status
    @PatchMapping("/tasks/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable UUID id,
            @RequestBody Task.TaskStatus status) {

        TaskResponseDTO response =
                taskService.updateTaskStatus(id, status);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<SubTaskDTO> addSubTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody SubTaskRequestDTO request) {

        SubTaskDTO response =
                taskService.addSubTask(taskId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // GET /api/tasks/{taskId}/subtasks
    @GetMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<List<SubTaskDTO>> getSubTasks(
            @PathVariable UUID taskId) {

        List<SubTaskDTO> response =
                taskService.getSubTasksByTask(taskId);

        return ResponseEntity.ok(response);
    }


    // PATCH /api/subtasks/{id}/toggle
    @PatchMapping("/subtasks/{id}/toggle")
    public ResponseEntity<SubTaskDTO> toggleSubTask(
            @PathVariable UUID id) {

        SubTaskDTO response =
                taskService.toggleSubTaskDone(id);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable UUID taskId,
            @Valid @RequestBody CommentRequestDTO request) {

        CommentDTO response =
                taskService.addComment(taskId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }



    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(
            @PathVariable UUID taskId) {

        List<CommentDTO> response =
                taskService.getCommentsByTask(taskId);

        return ResponseEntity.ok(response);
    }
}
