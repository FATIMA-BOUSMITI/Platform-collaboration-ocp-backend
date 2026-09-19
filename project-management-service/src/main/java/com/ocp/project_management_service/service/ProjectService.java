package com.ocp.project_management_service.service;
import com.ocp.project_management_service.dto.request.ProjectRequestDTO;
import com.ocp.project_management_service.dto.request.ProjectUpdateRequestDTO;
import com.ocp.project_management_service.dto.response.ProjectResponseDTO;
import com.ocp.project_management_service.entity.Project;
import com.ocp.project_management_service.exception.InvalidDateRangeException;
import com.ocp.project_management_service.exception.ProjectNotCompletedException;
import com.ocp.project_management_service.exception.ProjectNotFoundException;
import com.ocp.project_management_service.mapper.ProjectMapper;
import com.ocp.project_management_service.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List ;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository ;
    private final ProjectMapper projectMapper;

    //CREATION
    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO request){


        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new InvalidDateRangeException();
        }

        Project project = Project.builder().
        name(request.getName()).
        description(request.getDescription()).
        departmentId(request.getDepartmentId()).
        responsibleId(request.getResponsibleId()).
        projectPriority(request.getProjectPriority()).
        budget(request.getBudget()).
                reference(UUID.randomUUID().toString()).
                status(Project.ProjectStatus.PLANNED).
        startDate(request.getStartDate()).
        endDate(request.getEndDate()).build();


        Project savedProject= projectRepository.save(project);
        return projectMapper.toResponseDTO(savedProject);

    }

    //LECTURE
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(UUID id){
          Project project= projectRepository.findById(id).orElseThrow(
                  () ->  new ProjectNotFoundException(id.toString()));
        return  projectMapper.toResponseDTO(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAllProjects(){
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponseDTO).collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO>  getProjectsByDepartment(UUID departmentId){
         return projectRepository.findByDepartmentId(departmentId).stream()
                 .map(projectMapper::toResponseDTO).collect(Collectors.toList());
    }

    //UPDATE
    @Transactional
    public  ProjectResponseDTO updateProject(UUID id, ProjectUpdateRequestDTO request){

        Project project = projectRepository.findById(id).orElseThrow(
                ()-> new ProjectNotFoundException(id.toString())
        );

        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getResponsibleId() != null) {
            project.setResponsibleId(request.getResponsibleId());
        }
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }
        if (request.getProjectPriority() != null) {
            project.setProjectPriority(request.getProjectPriority());
        }
        if (request.getBudget() != null) {
            project.setBudget(request.getBudget());
        }

        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponseDTO(savedProject);


    }

    @Transactional
    public ProjectResponseDTO updateStatus(UUID id, Project.ProjectStatus status){
        Project project = projectRepository.findById(id).orElseThrow(
                ()-> new ProjectNotFoundException(id.toString())
        );
        project.setStatus(status);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDTO(savedProject);
    }

    //ARCHIVE
    @Transactional
     public void archiveProject(UUID id){
         Project project = projectRepository.findById(id).orElseThrow(
                 ()-> new ProjectNotFoundException(id.toString())
         );
         if(project.getStatus()!=Project.ProjectStatus.COMPLETED){
            throw  new ProjectNotCompletedException(id.toString());
         }
         project.setStatus(Project.ProjectStatus.ARCHIVED);
         projectRepository.save(project);
     }

     //DELETE
     @Transactional
    public void deleteProject(UUID id){

        Project project = projectRepository.findById(id).orElseThrow(
                ()-> new ProjectNotFoundException(id.toString())
        );
        projectRepository.delete(project);
    }







}
