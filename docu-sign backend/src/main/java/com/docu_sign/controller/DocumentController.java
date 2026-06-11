package com.docu_sign.controller;


import com.docu_sign.dto.DownloadedFile;
import com.docu_sign.dto.SendSignatureResponse;
import com.docu_sign.dto.UploadDocumentResponse;
import com.docu_sign.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public UploadDocumentResponse uploadDocument( @RequestParam("file") MultipartFile file ) {
        return documentService.uploadDocument(file);
    }

    @GetMapping
    public List<UploadDocumentResponse> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @GetMapping("/{id}")
    public UploadDocumentResponse getDocumentById(@PathVariable UUID id) {
        return documentService.getDocumentById(id);
    }


    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID id ) {

        DownloadedFile downloadedFile = documentService.downloadDocument(id);

        return ResponseEntity.ok()
                .contentType( MediaType.parseMediaType(downloadedFile.contentType()))
                .contentLength(downloadedFile.fileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadedFile.fileName() + "\"")
                .body(downloadedFile.resource());
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<SendSignatureResponse> sendSignatureRequest( @PathVariable UUID id) {

        return ResponseEntity.ok( documentService.sendSignatureRequest(id) );
    }
}
