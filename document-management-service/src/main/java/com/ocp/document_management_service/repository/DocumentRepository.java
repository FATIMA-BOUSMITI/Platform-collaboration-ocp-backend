package com.ocp.document_management_service.repository;

import com.ocp.document_management_service.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByOwnerId(UUID ownerId);

    List<Document> findByNameContainingIgnoreCase(String query);
}
