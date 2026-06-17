package com.docu_sign.service;

import com.docu_sign.dto.DownloadedFile;
import com.docu_sign.dto.SendSignatureResponse;
import com.docu_sign.dto.UploadDocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    UploadDocumentResponse uploadDocument(MultipartFile file);

    List<UploadDocumentResponse> getAllDocuments();

    UploadDocumentResponse getDocumentById(UUID id);

    DownloadedFile downloadDocument(UUID id);

    SendSignatureResponse sendSignatureRequest(UUID id);

    DownloadedFile downloadSignedDocument(UUID id);

    DownloadedFile downloadCertificate( UUID id );
}
