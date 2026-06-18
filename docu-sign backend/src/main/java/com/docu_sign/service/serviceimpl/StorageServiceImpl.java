package com.docu_sign.service.serviceimpl;


import com.docu_sign.config.SupabaseConfig;
import com.docu_sign.dto.DownloadedFile;
import com.docu_sign.exception.StorageException;
import com.docu_sign.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
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

    private final java.util.Map<String, byte[]> fileBytesCache = new java.util.concurrent.ConcurrentHashMap<>();

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

            headers.set(
                    "x-upsert",
                    "true"
            );

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
                throw new StorageException(
                        "Failed to upload file to Supabase"
                );
            }

            return fileName;

        } catch (Exception e) {

            throw new StorageException(
                    "Error uploading file",
                    e
            );
        }
    }

    @Override
    public DownloadedFile downloadFile(String storagePath, String originalFileName, String contentType, long fileSize) {

        try {

            String downloadUrl =
                    supabaseConfig.getUrl()
                            + "/storage/v1/object/"
                            + supabaseConfig.getBucket()
                            + "/"
                            + storagePath;

            HttpHeaders headers = new HttpHeaders();

            headers.set(
                    "Authorization",
                    "Bearer " + supabaseConfig.getApiKey()
            );

            headers.set(
                    "apikey",
                    supabaseConfig.getApiKey()
            );

            HttpEntity<Void> requestEntity =
                    new HttpEntity<>(headers);

            ResponseEntity<byte[]> response =
                    restTemplate.exchange(
                            downloadUrl,
                            HttpMethod.GET,
                            requestEntity,
                            byte[].class
                    );

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null) {

                throw new StorageException(
                        "Failed to download file from storage"
                );
            }



            return new DownloadedFile(
                    new ByteArrayResource(response.getBody()),
                    originalFileName,
                    contentType,
                    response.getBody().length
            );

        } catch (Exception e) {

            throw new StorageException(
                    "Error downloading file",
                    e
            );
        }
    }

    @Override
    public byte[] downloadFileBytes(String storagePath) {
        if (storagePath == null) {
            return new byte[0];
        }
        if (fileBytesCache.containsKey(storagePath)) {
            return fileBytesCache.get(storagePath);
        }
        try {

            String downloadUrl =
                    supabaseConfig.getUrl()
                            + "/storage/v1/object/"
                            + supabaseConfig.getBucket()
                            + "/"
                            + storagePath;

            HttpHeaders headers = new HttpHeaders();

            headers.set(
                    "Authorization",
                    "Bearer " + supabaseConfig.getApiKey()
            );

            headers.set(
                    "apikey",
                    supabaseConfig.getApiKey()
            );


            HttpEntity<Void> requestEntity =
                    new HttpEntity<>(headers);

            ResponseEntity<byte[]> response =
                    restTemplate.exchange(
                            downloadUrl,
                            HttpMethod.GET,
                            requestEntity,
                            byte[].class
                    );

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null) {

                throw new StorageException(
                        "Failed to download file from storage"
                );
            }

            byte[] bytes = response.getBody();
            fileBytesCache.put(storagePath, bytes);
            return bytes;

        } catch (Exception e) {

            throw new StorageException(
                    "Error downloading file",
                    e
            );
        }
    }

    @Override
    public String uploadBytes(byte[] bytes, String fileName, String contentType) {

        if (fileName != null) {
            fileBytesCache.remove(fileName);
        }

        try {

            String uploadUrl = supabaseConfig.getUrl()
                            + "/storage/v1/object/"
                            + supabaseConfig.getBucket()
                            + "/"
                            + fileName;

            HttpHeaders headers = new HttpHeaders();

            headers.set("Authorization",
                    "Bearer " + supabaseConfig.getApiKey()
            );

            headers.set(
                    "apikey",
                    supabaseConfig.getApiKey()
            );

            headers.set(
                    "x-upsert",
                    "true"
            );

            headers.setContentType(
                    MediaType.parseMediaType(contentType)
            );

            HttpEntity<byte[]> requestEntity =
                    new HttpEntity<>(
                            bytes,
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
                throw new StorageException(
                        "Failed to upload file to Supabase"
                );
            }

            return fileName;

        } catch (Exception e) {

            throw new StorageException(
                    "Error uploading file",
                    e
            );
        }
    }
}
