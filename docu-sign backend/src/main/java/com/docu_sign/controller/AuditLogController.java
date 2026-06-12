package com.docu_sign.controller;


import com.docu_sign.dto.AuditLogResponse;
import com.docu_sign.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/{documentId}/audit")
    public List<AuditLogResponse> getAuditHistory( @PathVariable UUID documentId ) {
        System.out.println("AUDIT ENDPOINT HIT");
        return auditLogService.getDocumentAuditHistory(documentId);
    }

}
