package com.ocp.project_management_service.repository;
import com.ocp.project_management_service.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BoardColumnRepository extends JpaRepository<BoardColumn,UUID>{List<BoardColumn> findByProjectIdOrderByPositionAscIdAsc(UUID projectId);}
