package com.docu_sign.repo;

import com.docu_sign.entity.Document;
import com.docu_sign.entity.SignatureField;
import com.docu_sign.entity.Signer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SignatureFieldRepository extends JpaRepository<SignatureField, UUID> {

    List<SignatureField> findByDocument(Document document);

    List<SignatureField> findBySigner(Signer signer);

    Optional<SignatureField> findByIdAndDocument( UUID id, Document document );

    boolean existsByDocument(Document document);
}
