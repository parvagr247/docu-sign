package com.docu_sign.service.serviceimpl;


import com.docu_sign.config.AppProperties;
import com.docu_sign.dto.DownloadedFile;
import com.docu_sign.dto.SendSignatureResponse;
import com.docu_sign.dto.SignatureRequestEmail;
import com.docu_sign.dto.UploadDocumentResponse;
import com.docu_sign.entity.*;
import com.docu_sign.exception.BusinessValidationException;
import com.docu_sign.exception.ResourceNotFoundException;
import com.docu_sign.repo.DocumentRepository;
import com.docu_sign.repo.SignerRepository;
import com.docu_sign.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    private final CurrentUserService currentUserService;
    private final SignerRepository signerRepository;
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final AuditLogService auditLogService;

    @Override
    public UploadDocumentResponse uploadDocument(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are allowed");
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

        auditLogService.logEvent(
                savedDocument,
                null,
                AuditEventType.DOCUMENT_UPLOADED,
                "Document uploaded"
        );

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
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        return UploadDocumentResponse.builder()
                .id(document.getId())
                .originalFileName(document.getOriginalFileName())
                .fileSize(document.getFileSize())
                .status(document.getStatus())
                .uploadedAt(document.getUploadedAt())
                .build();
    }

    @Override
    public DownloadedFile downloadDocument(UUID id) {
        User currentUser = currentUserService.getCurrentUser();

        Document document = documentRepository
                .findByIdAndUploadedBy(id, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Document not found"
                        ));

        return storageService.downloadFile(
                document.getStoragePath(),
                document.getOriginalFileName(),
                document.getContentType(),
                document.getFileSize()
        );
    }

    @Override
    public SendSignatureResponse sendSignatureRequest(UUID id) {

        User currentUser = currentUserService.getCurrentUser();

        Document document = documentRepository
                .findByIdAndUploadedBy(id, currentUser)
                .orElseThrow(() ->  new ResourceNotFoundException( "Document not found"));

        if (!signerRepository.existsByDocument(document)) {
            throw new BusinessValidationException( "Document must contain at least one signer");
        }

        if (document.getStatus() != DocumentStatus.UPLOADED) {
            throw new BusinessValidationException( "Document cannot be sent in current status" );
        }

        List<Signer> signers = signerRepository.findByDocument(document);

        document.setStatus( DocumentStatus.PENDING_SIGNATURE );
        document.setSignatureRequestedAt( LocalDateTime.now() );
        Document savedDocument = documentRepository.save(document);

        auditLogService.logEvent(
                savedDocument,
                null,
                AuditEventType.SIGNATURE_REQUESTED,
                "Signature workflow started"
        );

        for (Signer signer : signers) {

            System.out.println("STEP 4");

            String signingUrl =
                    appProperties.getFrontendUrl()
                            + "/sign/"
                            + signer.getSigningToken();

            SignatureRequestEmail email =
                    new SignatureRequestEmail(
                            signer.getName(),
                            signer.getEmail(),
                            document.getOriginalFileName(),
                            signingUrl
                    );

            emailService.sendSignatureRequestEmail(email);

            auditLogService.logEvent(
                    document,
                    signer,
                    AuditEventType.EMAIL_SENT,
                    signer.getEmail()
            );
        }

        return new SendSignatureResponse(
                savedDocument.getId(),
                savedDocument.getStatus(),
                savedDocument.getSignatureRequestedAt()
        );

    }

    @Override
    public DownloadedFile downloadSignedDocument(UUID id) {
        User currentUser = currentUserService.getCurrentUser();

        Document document =
                documentRepository.findByIdAndUploadedBy(id, currentUser )
                        .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (document.getSignedDocumentPath() == null) {
            throw new BusinessValidationException( "Signed document not available" );
        }

        System.out.println("Original Size = " + document.getFileSize());

        return storageService.downloadFile(
                document.getSignedDocumentPath(),
                "signed_" + document.getOriginalFileName(),
                document.getContentType(),
                document.getFileSize()
        );
    }
}
