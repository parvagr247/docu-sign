package com.docu_sign.repo;

import com.docu_sign.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository <Document, UUID> {

    List<Document> findByUploadedById(UUID userId);
}
