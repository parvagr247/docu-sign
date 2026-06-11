package com.docu_sign.dto;

import org.springframework.core.io.Resource;

public record DownloadedFile(
        Resource resource,
        String fileName,
        String contentType,
        long fileSize
) {
}
