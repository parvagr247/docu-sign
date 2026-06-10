package com.docu_sign.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

        String uploadFile(MultipartFile file);

}
