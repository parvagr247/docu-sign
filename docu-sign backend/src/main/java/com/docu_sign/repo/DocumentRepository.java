package com.docu_sign.repo;

import com.docu_sign.entity.Document;
import com.docu_sign.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository <Document, UUID> {

    List<Document> findByUploadedById(UUID userId);

    List<Document> findByUploadedBy(User user);

    Optional<Document> findByIdAndUploadedBy(UUID id, User uploadedBy);
}
