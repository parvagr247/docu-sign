package com.docu_sign.service;

import com.docu_sign.dto.DownloadedFile;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

        String uploadFile(MultipartFile file);

        DownloadedFile downloadFile(
                String storagePath,
                String originalFileName,
                String contentType,
                long fileSize
        );

        byte[] downloadFileBytes(String storagePath);

        String uploadBytes( byte[] bytes, String fileName, String contentType );

}
