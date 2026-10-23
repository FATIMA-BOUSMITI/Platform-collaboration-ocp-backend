package com.ocp.auth_service.repository;
import com.ocp.auth_service.entity.AdminNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface AdminNoticeRepository extends JpaRepository<AdminNotice,UUID>{List<AdminNotice> findByOwnerOrderByCreatedAtDesc(UUID owner);void deleteByOwner(UUID owner);}
