package com.docu_sign.controller;


import com.docu_sign.dto.UploadDocumentResponse;
import com.docu_sign.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public UploadDocumentResponse getDocumentById(
            @PathVariable UUID id
    ) {

        return documentService.getDocumentById(id);
    }
}
