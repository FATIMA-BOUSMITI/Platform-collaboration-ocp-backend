package com.ocp.document_management_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ocp.document_management_service.dto.response.DocumentResponseDTO;
import com.ocp.document_management_service.entity.Document;
import com.ocp.document_management_service.exception.DocumentNotFoundException;
import com.ocp.document_management_service.exception.DocumentUploadException;
import com.ocp.document_management_service.mapper.DocumentMapper;
import com.ocp.document_management_service.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final Cloudinary cloudinary;

    @Transactional
    public DocumentResponseDTO uploadDocument(MultipartFile file, UUID ownerId, String customName) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")
            );

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            Document document = Document.builder()
                    .name((customName != null && !customName.isBlank()) ? customName : file.getOriginalFilename())
                    .type(resolveType(file.getContentType()))
                    .sizeBytes(file.getSize())
                    .cloudinaryUrl(url)
                    .cloudinaryPublicId(publicId)
                    .ownerId(ownerId)
                    .status(Document.DocumentStatus.DRAFT)
                    .build();

            Document savedDocument = documentRepository.save(document);
            return documentMapper.toResponse(savedDocument);

        } catch (IOException e) {
            throw new DocumentUploadException("Échec de l'upload vers Cloudinary : " + e.getMessage());
        }
    }

    private Document.DocumentType resolveType(String contentType) {
        if (contentType == null) return Document.DocumentType.OTHER;
        if (contentType.equals("application/pdf")) return Document.DocumentType.PDF;
        if (contentType.contains("word")) return Document.DocumentType.WORD;
        if (contentType.contains("sheet") || contentType.contains("excel")) return Document.DocumentType.EXCEL;
        if (contentType.contains("presentation")) return Document.DocumentType.PPT;
        if (contentType.startsWith("image")) return Document.DocumentType.IMAGE;
        if (contentType.startsWith("video")) return Document.DocumentType.VIDEO;
        if (contentType.contains("zip")) return Document.DocumentType.ZIP;
        return Document.DocumentType.OTHER;
    }

    @Transactional(readOnly = true)
    public DocumentResponseDTO getDocumentById(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id.toString()));
        return documentMapper.toResponse(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getDocumentsByOwner(UUID ownerId) {
        return documentRepository.findByOwnerId(ownerId).stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> searchDocuments(String query) {
        return documentRepository.findByNameContainingIgnoreCase(query).stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentResponseDTO validateDocument(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id.toString()));
        document.setStatus(Document.DocumentStatus.VALIDATED);
        Document savedDocument = documentRepository.save(document);
        return documentMapper.toResponse(savedDocument);
    }

    @Transactional
    public void deleteDocument(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id.toString()));

        try {
            cloudinary.uploader().destroy(document.getCloudinaryPublicId(), ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new DocumentUploadException("Échec de la suppression sur Cloudinary : " + e.getMessage());
        }

        documentRepository.delete(document);
    }
}
