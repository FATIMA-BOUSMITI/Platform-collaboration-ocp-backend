package com.ocp.project_management_service.service;

import com.ocp.project_management_service.dto.request.CommentRequestDTO;
import com.ocp.project_management_service.dto.request.SubTaskRequestDTO;
import com.ocp.project_management_service.dto.request.TaskRequestDTO;
import com.ocp.project_management_service.dto.response.CommentDTO;
import com.ocp.project_management_service.dto.response.SubTaskDTO;
import com.ocp.project_management_service.dto.response.TaskResponseDTO;
import com.ocp.project_management_service.entity.Project;
import com.ocp.project_management_service.entity.SubTask;
import com.ocp.project_management_service.entity.Task;
import com.ocp.project_management_service.entity.TaskComment;
import com.ocp.project_management_service.exception.ProjectNotFoundException;
import com.ocp.project_management_service.exception.SubTaskNotFoundException;
import com.ocp.project_management_service.exception.TaskNotFoundException;
import com.ocp.project_management_service.mapper.CommentMapper;
import com.ocp.project_management_service.mapper.SubTaskMapper;
import com.ocp.project_management_service.mapper.TaskMapper;
import com.ocp.project_management_service.repository.ProjectRepository;
import com.ocp.project_management_service.repository.SubTaskRepository;
import com.ocp.project_management_service.repository.TaskCommentRepository;
import com.ocp.project_management_service.repository.TaskRepository;
import com.ocp.project_management_service.security.AuthenticatedUser;
import com.ocp.project_management_service.security.ProjectAccessPolicy;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor

public class TaskService {

    private final TaskRepository taskRepository ;
    private final TaskMapper taskMapper ;
    private  final ProjectRepository projectRepository;
    private  final SubTaskRepository subTaskRepository;
    private  final TaskCommentRepository taskCommentRepository;
    private  final CommentMapper commentMapper ;
    private final SubTaskMapper subTaskMapper;
    private final ProjectAccessPolicy projectAccessPolicy;
    private final BoardService boardService;
    private final CollaborationEvents collaborationEvents;

    // creation
    @Transactional
    public TaskResponseDTO createTask(
            UUID projectId, TaskRequestDTO request, AuthenticatedUser authenticatedUser){


        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId.toString()));
        projectAccessPolicy.requireProjectManagement(project, authenticatedUser);

           Task task = Task.builder().
                title(request.getTitle()).
                description(request.getDescription()==null?"":request.getDescription()).
                assigneeId(request.getAssigneeId()).
                priority(request.getPriority()).
                dueDate(request.getDueDate()).
                   project(project).
                estimatedTime(Duration.ofMinutes(request.getEstimatedTimeMinutes()==null?0:request.getEstimatedTimeMinutes())).
                realTime(Duration.ZERO).
                status(Task.TaskStatus.TODO).build();

        boardService.assignInitialColumn(task,request.getColumnId(),authenticatedUser);
        Task savedTask = taskRepository.save(task);
        collaborationEvents.task(savedTask,savedTask.getAssigneeId()==null?"TASK_UPDATED":"TASK_ASSIGNED",authenticatedUser==null?null:authenticatedUser.userId());
        return taskMapper.toResponseDTO(savedTask);

       }


    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(UUID id, AuthenticatedUser authenticatedUser){

        Task task = taskRepository.findById(id).orElseThrow(
                ()->new TaskNotFoundException(id.toString())
        );
        projectAccessPolicy.requireTaskRead(task, authenticatedUser);
        return taskMapper.toResponseDTO(task);
    }


    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByProject(
            UUID projectId, AuthenticatedUser authenticatedUser){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId.toString()));
        projectAccessPolicy.requireProjectRead(project, authenticatedUser);

        return taskRepository.findByProjectId(projectId).stream()
                .filter(task -> projectAccessPolicy.canReadTask(task, authenticatedUser))
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByProjectAndStatus(
            UUID projectId, Task.TaskStatus status, AuthenticatedUser authenticatedUser){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId.toString()));
        projectAccessPolicy.requireProjectRead(project, authenticatedUser);

        return taskRepository.findByProjectIdAndStatus(projectId, status).stream()
                .filter(task -> projectAccessPolicy.canReadTask(task, authenticatedUser))
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


     //mes taches
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByAssignee(
            UUID userId, AuthenticatedUser authenticatedUser){
        if (authenticatedUser != null
                && !authenticatedUser.isAdministratorOrDirector()
                && !authenticatedUser.isManager()
                && !(authenticatedUser.isEmployee()
                    && authenticatedUser.userId().equals(userId))) {
            throw new AccessDeniedException("Task assignments are not visible to this user");
        }
        return taskRepository.findByAssigneeId(userId).stream()
                .filter(task -> projectAccessPolicy.canReadTask(task, authenticatedUser))
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    //UPDATE
    @Transactional
    public TaskResponseDTO updateTaskStatus(
            UUID id, Task.TaskStatus status, AuthenticatedUser authenticatedUser){
        Task task = taskRepository.findById(id).orElseThrow(
                ()-> new TaskNotFoundException(id.toString())
        );
        projectAccessPolicy.requireTaskStatusChange(task, authenticatedUser);
        if(status!= null) {
            task.setStatus(status);
            task.setColumnId(null);
        }
        Task savedTask= taskRepository.save(task);
        collaborationEvents.task(savedTask,"TASK_STATUS",authenticatedUser==null?null:authenticatedUser.userId());
        return taskMapper.toResponseDTO(savedTask);

    }

    @Transactional
    public TaskResponseDTO updateTask(
            UUID id, TaskRequestDTO request, AuthenticatedUser authenticatedUser){
        Task task = taskRepository.findById(id).orElseThrow(
                ()-> new TaskNotFoundException(id.toString())
        );
        projectAccessPolicy.requireTaskManagement(task, authenticatedUser);
        if(request.getTitle()!= null) {
            task.setTitle(request.getTitle());
        }
        if(request.getDescription()!= null) {
            task.setDescription(request.getDescription());
        }
        UUID previousAssignee=task.getAssigneeId();
        task.setAssigneeId(request.getAssigneeId());
        if(request.getPriority()!= null) {
            task.setPriority(request.getPriority());
        }
        task.setDueDate(request.getDueDate());
        if(request.getEstimatedTimeMinutes()!= null) {
            task.setEstimatedTime(Duration.ofMinutes(request.getEstimatedTimeMinutes()));
        }
        Task savedTask= taskRepository.save(task);
        collaborationEvents.task(savedTask,!java.util.Objects.equals(previousAssignee,savedTask.getAssigneeId())&&savedTask.getAssigneeId()!=null?"TASK_ASSIGNED":"TASK_UPDATED",authenticatedUser==null?null:authenticatedUser.userId());
        return taskMapper.toResponseDTO(savedTask);

    }

    @Transactional
    public SubTaskDTO addSubTask(
            UUID taskId, SubTaskRequestDTO request, AuthenticatedUser authenticatedUser){


        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));
        projectAccessPolicy.requireTaskWork(task, authenticatedUser);

        SubTask subTask= SubTask.builder()
                .title(request.getTitle())
                .isDone(false)
                .task(task)
                .build();

        SubTask subTaskSaved = subTaskRepository.save(subTask);
        return subTaskMapper.toResponseDTO(subTaskSaved);
    }

    @Transactional
    public SubTaskDTO toggleSubTaskDone(UUID subTaskId, AuthenticatedUser authenticatedUser){


        SubTask subTask= subTaskRepository.findById(subTaskId).orElseThrow(
                ()-> new SubTaskNotFoundException(subTaskId.toString())
        );
        projectAccessPolicy.requireTaskStatusChange(subTask.getTask(), authenticatedUser);

        subTask.setDone(!subTask.isDone());
        SubTask subTask1 = subTaskRepository.save(subTask);

        return subTaskMapper.toResponseDTO(subTask1);
    }

    @Transactional(readOnly = true)
    public List<SubTaskDTO> getSubTasksByTask(
            UUID taskId, AuthenticatedUser authenticatedUser){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));
        projectAccessPolicy.requireTaskRead(task, authenticatedUser);
        return subTaskRepository.findByTaskId(taskId).stream()
                .map(subTaskMapper::toResponseDTO).collect(Collectors.toList());

    }

    @Transactional(readOnly=true) public java.util.Set<UUID> mentionCandidates(UUID taskId,AuthenticatedUser user){var task=taskRepository.findById(taskId).orElseThrow(()->new TaskNotFoundException(taskId.toString()));projectAccessPolicy.requireTaskRead(task,user);return collaborationEvents.mentionCandidates(task);}
    @Transactional
    public CommentDTO addComment(
            UUID taskId, CommentRequestDTO request, AuthenticatedUser authenticatedUser){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));

        if (authenticatedUser != null
                && !authenticatedUser.userId().equals(request.getAuthorId())) {
            throw new AccessDeniedException("Comment author must match the authenticated user");
        }
        projectAccessPolicy.requireTaskWork(task, authenticatedUser);

        var mentions=request.getMentionIds()==null?java.util.Set.<UUID>of():request.getMentionIds();
        for(UUID target:mentions)if(!target.equals(task.getAssigneeId())&&!target.equals(task.getProject().getResponsibleId())&&!projectAccessPolicy.isInvited(task.getProject().getId(),target))throw new AccessDeniedException("La mention doit cibler un collaborateur autorisé sur cette tâche");
        TaskComment taskComment= TaskComment.builder()
                .content(request.getContent())
                .authorId(request.getAuthorId())
                .task(task)
                .build();
        TaskComment taskCommentSaved=taskCommentRepository.save(taskComment);
        collaborationEvents.comment(task,taskCommentSaved,authenticatedUser==null?request.getAuthorId():authenticatedUser.userId(),mentions);

        return commentMapper.toResponse(taskCommentSaved);

    }

    @Transactional(readOnly = true)
     public List<CommentDTO> getCommentsByTask(
             UUID taskId, AuthenticatedUser authenticatedUser){

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));
        projectAccessPolicy.requireTaskRead(task, authenticatedUser);

        return taskCommentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(commentMapper::toResponse).
                collect(Collectors.toList());

    }

}
