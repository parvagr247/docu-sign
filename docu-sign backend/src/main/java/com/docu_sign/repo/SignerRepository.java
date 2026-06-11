package com.docu_sign.repo;


import com.docu_sign.entity.Document;
import com.docu_sign.entity.Signer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SignerRepository extends JpaRepository<Signer, UUID> {

    List<Signer> findByDocument(Document document);

    boolean existsByDocumentAndEmail(Document document, String email);

    Optional<Signer> findBySigningToken(String signingToken);

    boolean existsByDocument(Document document);

}
