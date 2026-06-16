package com.docu_sign.repo;

import com.docu_sign.entity.FieldCompletion;
import com.docu_sign.entity.SignatureField;
import com.docu_sign.entity.Signer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FieldCompletionRepository extends JpaRepository<FieldCompletion, UUID> {

    List<FieldCompletion> findBySigner(
            Signer signer
    );

    Optional<FieldCompletion>
    findBySignatureField(
            SignatureField signatureField
    );

    boolean existsBySignatureField(
            SignatureField signatureField
    );
}
