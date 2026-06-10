package com.docu_sign.service.serviceimpl;


import com.docu_sign.config.SupabaseConfig;
import com.docu_sign.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final SupabaseConfig supabaseConfig;
    private final RestTemplate restTemplate;

    @Override
    public String uploadFile(MultipartFile file) {

        try {

            String fileName = UUID.randomUUID() + ".pdf";

            String uploadUrl =
                    supabaseConfig.getUrl()
                            + "/storage/v1/object/"
                            + supabaseConfig.getBucket()
                            + "/"
                            + fileName;

            HttpHeaders headers = new HttpHeaders();

            headers.set("Authorization",
                    "Bearer " + supabaseConfig.getApiKey());

            headers.set("apikey",
                    supabaseConfig.getApiKey());

            headers.setContentType(
                    MediaType.APPLICATION_PDF
            );

            HttpEntity<byte[]> requestEntity =
                    new HttpEntity<>(
                            file.getBytes(),
                            headers
                    );

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            uploadUrl,
                            HttpMethod.POST,
                            requestEntity,
                            String.class
                    );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(
                        "Failed to upload file to Supabase"
                );
            }

            return fileName;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error uploading file",
                    e
            );
        }
    }
}
