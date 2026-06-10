package com.docu_sign.service.serviceimpl;


import com.docu_sign.dto.UploadDocumentResponse;
import com.docu_sign.entity.Document;
import com.docu_sign.entity.DocumentStatus;
import com.docu_sign.entity.User;
import com.docu_sign.repo.DocumentRepository;
import com.docu_sign.service.CurrentUserService;
import com.docu_sign.service.DocumentService;
import com.docu_sign.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    private final CurrentUserService currentUserService;

    @Override
    public UploadDocumentResponse uploadDocument(MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        String storagePath = storageService.uploadFile(file);

        Document document = Document.builder()
                .originalFileName(file.getOriginalFilename())
                .storagePath(storagePath)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .status(DocumentStatus.UPLOADED)
                .uploadedBy(currentUserService.getCurrentUser())
                .build();

        Document savedDocument =
                documentRepository.save(document);

        return UploadDocumentResponse.builder()
                .id(savedDocument.getId())
                .originalFileName(savedDocument.getOriginalFileName())
                .fileSize(savedDocument.getFileSize())
                .status(savedDocument.getStatus())
                .uploadedAt(savedDocument.getUploadedAt())
                .build();
    }

    @Override
    public List<UploadDocumentResponse> getAllDocuments() {

        User currentUser = currentUserService.getCurrentUser();

        return documentRepository
                .findByUploadedBy(currentUser)
                .stream()
                .map(document ->
                        UploadDocumentResponse.builder()
                                .id(document.getId())
                                .originalFileName(document.getOriginalFileName())
                                .fileSize(document.getFileSize())
                                .status(document.getStatus())
                                .uploadedAt(document.getUploadedAt())
                                .build()
                )
                .toList();
    }

    @Override
    public UploadDocumentResponse getDocumentById(UUID id) {

        User currentUser = currentUserService.getCurrentUser();

        Document document = documentRepository.findByIdAndUploadedBy(id,currentUser)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return UploadDocumentResponse.builder()
                .id(document.getId())
                .originalFileName(document.getOriginalFileName())
                .fileSize(document.getFileSize())
                .status(document.getStatus())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}
