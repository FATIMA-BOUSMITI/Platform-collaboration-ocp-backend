package com.ocp.project_management_service.controller;
import com.ocp.project_management_service.service.BoardService;
import com.ocp.project_management_service.entity.BoardColumn;
import com.ocp.project_management_service.dto.response.TaskResponseDTO;
import com.ocp.project_management_service.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import java.util.*;
@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class BoardController {
 private final BoardService service;
 @GetMapping("/projects/{projectId}/columns")public List<BoardColumn> list(@PathVariable UUID projectId,@AuthenticationPrincipal AuthenticatedUser user){return service.list(projectId,user);}
 @PostMapping("/projects/{projectId}/columns")@ResponseStatus(HttpStatus.CREATED)public BoardColumn create(@PathVariable UUID projectId,@RequestBody BoardService.ColumnRequest request,@AuthenticationPrincipal AuthenticatedUser user){return service.create(projectId,request,user);}
 @PutMapping("/projects/{projectId}/columns/{id}")public BoardColumn update(@PathVariable UUID projectId,@PathVariable UUID id,@RequestBody BoardService.ColumnRequest request,@AuthenticationPrincipal AuthenticatedUser user){return service.update(projectId,id,request,user);}
 @DeleteMapping("/projects/{projectId}/columns/{id}")@ResponseStatus(HttpStatus.NO_CONTENT)public void delete(@PathVariable UUID projectId,@PathVariable UUID id,@AuthenticationPrincipal AuthenticatedUser user){service.delete(projectId,id,user);}
 @PatchMapping("/tasks/{id}/move")public TaskResponseDTO move(@PathVariable UUID id,@RequestBody BoardService.Move request,@AuthenticationPrincipal AuthenticatedUser user){return service.move(id,request,user);}
}
