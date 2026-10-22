package com.ocp.document_management_service.controller;

import com.ocp.document_management_service.dto.response.DocumentResponseDTO;
import com.ocp.document_management_service.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ownerId") UUID ownerId,
            @RequestParam(value = "name", required = false) String name) {
        DocumentResponseDTO response = documentService.uploadDocument(file, ownerId, name);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> getDocumentById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponseDTO>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<DocumentResponseDTO>> getDocumentsByOwner(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(documentService.getDocumentsByOwner(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponseDTO>> search(@RequestParam String query) {
        return ResponseEntity.ok(documentService.searchDocuments(query));
    }

    @PatchMapping("/{id}/validate")
    public ResponseEntity<DocumentResponseDTO> validateDocument(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.validateDocument(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}