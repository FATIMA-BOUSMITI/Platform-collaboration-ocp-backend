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
import lombok.AllArgsConstructor;
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

    // creation
    @Transactional
    public TaskResponseDTO createTask(UUID projectId, TaskRequestDTO request){


        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId.toString()));

           Task task = Task.builder().
                title(request.getTitle()).
                description(request.getDescription()).
                assigneeId(request.getAssigneeId()).
                priority(request.getPriority()).
                dueDate(request.getDueDate()).
                   project(project).
                estimatedTime(Duration.ofMinutes(request.getEstimatedTimeMinutes())).
                status(Task.TaskStatus.TODO).build();

        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponseDTO(savedTask);

       }


    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(UUID id){

        Task task = taskRepository.findById(id).orElseThrow(
                ()->new TaskNotFoundException(id.toString())
        );
        return taskMapper.toResponseDTO(task);
    }


    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByProject(UUID projectId){

        return  taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByProjectAndStatus(UUID projectId, Task.TaskStatus status){

        return  taskRepository.findByProjectIdAndStatus(projectId,status).stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


     //mes taches
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByAssignee(UUID userId){
        return  taskRepository.findByAssigneeId(userId).stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    //UPDATE
    @Transactional
    public TaskResponseDTO updateTaskStatus(UUID id, Task.TaskStatus status){
        Task task = taskRepository.findById(id).orElseThrow(
                ()-> new TaskNotFoundException(id.toString())
        );
        if(status!= null) {
            task.setStatus(status);
        }
        Task savedTask= taskRepository.save(task);
        return taskMapper.toResponseDTO(savedTask);

    }

    @Transactional
    public TaskResponseDTO updateTask(UUID id, TaskRequestDTO request){
        Task task = taskRepository.findById(id).orElseThrow(
                ()-> new TaskNotFoundException(id.toString())
        );
        if(request.getTitle()!= null) {
            task.setTitle(request.getTitle());
        }
        if(request.getDescription()!= null) {
            task.setDescription(request.getDescription());
        }
        if(request.getAssigneeId()!= null) {
            task.setAssigneeId(request.getAssigneeId());
        }
        if(request.getPriority()!= null) {
            task.setPriority(request.getPriority());
        }
        if(request.getDueDate()!= null) {
            task.setDueDate(request.getDueDate());
        }
        if(request.getEstimatedTimeMinutes()!= null) {
            task.setEstimatedTime(Duration.ofMinutes(request.getEstimatedTimeMinutes()));
        }
        Task savedTask= taskRepository.save(task);
        return taskMapper.toResponseDTO(savedTask);

    }

    @Transactional
    public SubTaskDTO addSubTask(UUID taskId, SubTaskRequestDTO request){


        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));

        SubTask subTask= SubTask.builder()
                .title(request.getTitle())
                .isDone(false)
                .task(task)
                .build();

        SubTask subTaskSaved = subTaskRepository.save(subTask);
        return subTaskMapper.toResponseDTO(subTaskSaved);
    }

    @Transactional
    public SubTaskDTO toggleSubTaskDone(UUID subTaskId){


        SubTask subTask= subTaskRepository.findById(subTaskId).orElseThrow(
                ()-> new SubTaskNotFoundException(subTaskId.toString())
        );

        subTask.setDone(!subTask.isDone());
        SubTask subTask1 = subTaskRepository.save(subTask);

        return subTaskMapper.toResponseDTO(subTask1);
    }

    @Transactional(readOnly = true)
    public List<SubTaskDTO> getSubTasksByTask(UUID taskId){

        if(!taskRepository.existsById(taskId)){
            throw new TaskNotFoundException(taskId.toString());
        }
        return subTaskRepository.findByTaskId(taskId).stream()
                .map(subTaskMapper::toResponseDTO).collect(Collectors.toList());

    }

    @Transactional
    public CommentDTO addComment(UUID taskId, CommentRequestDTO request){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));


        TaskComment taskComment= TaskComment.builder()
                .content(request.getContent())
                .authorId(request.getAuthorId())
                .task(task)
                .build();
        TaskComment taskCommentSaved=taskCommentRepository.save(taskComment);

        return commentMapper.toResponse(taskCommentSaved);

    }

    @Transactional(readOnly = true)
     public List<CommentDTO> getCommentsByTask(UUID taskId){

        if(!taskRepository.existsById(taskId)){
            throw new TaskNotFoundException(taskId.toString());
        }

        return taskCommentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(commentMapper::toResponse).
                collect(Collectors.toList());

    }

}
