package com.docu_sign.service.serviceimpl;

import com.docu_sign.dto.AuditLogResponse;
import com.docu_sign.entity.*;
import com.docu_sign.exception.ResourceNotFoundException;
import com.docu_sign.repo.AuditLogRepository;
import com.docu_sign.repo.DocumentRepository;
import com.docu_sign.service.AuditLogService;
import com.docu_sign.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserService currentUserService;
    private final DocumentRepository documentRepository;

    @Override
    public void logEvent(Document document, Signer signer, AuditEventType eventType, String metadata) {

        AuditLog auditLog = AuditLog.builder()
                        .document(document)
                        .signer(signer)
                        .eventType(eventType)
                        .metadata(metadata)
                        .build();

        auditLogRepository.save(auditLog);

    }

    @Override
    public List<AuditLogResponse> getDocumentAuditHistory(UUID documentId) {
        User currentUser =
                currentUserService.getCurrentUser();

        Document document =
                documentRepository
                        .findByIdAndUploadedBy( documentId, currentUser )
                        .orElseThrow(() -> new ResourceNotFoundException( "Document not found" ) );

        return auditLogRepository
                .findByDocumentOrderByCreatedAtAsc(document)
                .stream()
                .map(log ->
                        AuditLogResponse.builder()
                                .id(log.getId())
                                .eventType(log.getEventType().name())
                                .metadata(log.getMetadata())
                                .signerName(
                                        log.getSigner() != null
                                                ? log.getSigner().getName()
                                                : null
                                )
                                .createdAt(log.getCreatedAt())
                                .build()
                )
                .toList();
    }
}
