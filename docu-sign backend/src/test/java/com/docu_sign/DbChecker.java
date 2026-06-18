package com.docu_sign;

import com.docu_sign.entity.*;
import com.docu_sign.repo.*;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
class DbChecker {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private SignerRepository signerRepository;

    @Autowired
    private SignatureFieldRepository signatureFieldRepository;

    @Autowired
    private FieldCompletionRepository fieldCompletionRepository;

    @Test
    void checkDatabase() {
        UUID docId = UUID.fromString("3d3f35e4-a49a-4cd2-9a53-26e2436d8490");
        System.out.println("[DIAGNOSTIC] === CHECKING DATABASE ===");
        
        documentRepository.findById(docId).ifPresentOrElse(doc -> {
            System.out.println("[DIAGNOSTIC] Document: " + doc.getOriginalFileName());
            System.out.println("[DIAGNOSTIC] Status: " + doc.getStatus());
            System.out.println("[DIAGNOSTIC] Storage Path: " + doc.getStoragePath());
            System.out.println("[DIAGNOSTIC] Signed Path: " + doc.getSignedDocumentPath());
            
            List<Signer> signers = signerRepository.findByDocument(doc);
            System.out.println("[DIAGNOSTIC] Signers Count: " + signers.size());
            for (Signer s : signers) {
                System.out.println("[DIAGNOSTIC]   - Signer Name: " + s.getName());
                System.out.println("[DIAGNOSTIC]     Email: " + s.getEmail());
                System.out.println("[DIAGNOSTIC]     Status: " + s.getStatus());
                System.out.println("[DIAGNOSTIC]     Signature Image Path: " + s.getSignatureImagePath());
                System.out.println("[DIAGNOSTIC]     Token: " + s.getSigningToken());
            }
            
            List<SignatureField> fields = signatureFieldRepository.findByDocument(doc);
            System.out.println("[DIAGNOSTIC] Fields Count: " + fields.size());
            for (SignatureField f : fields) {
                System.out.println("[DIAGNOSTIC]   - Field ID: " + f.getId());
                System.out.println("[DIAGNOSTIC]     Signer Name: " + f.getSigner().getName());
                System.out.println("[DIAGNOSTIC]     Page: " + f.getPageNumber());
                System.out.println("[DIAGNOSTIC]     Coords: X=" + f.getXPosition() + ", Y=" + f.getYPosition());
                System.out.println("[DIAGNOSTIC]     Size: W=" + f.getWidth() + ", H=" + f.getHeight());
                
                boolean isCompleted = fieldCompletionRepository.existsBySignatureField(f);
                System.out.println("[DIAGNOSTIC]     Completed: " + isCompleted);
            }
        }, () -> {
            System.out.println("[DIAGNOSTIC] Document not found!");
        });
        System.out.println("[DIAGNOSTIC] =========================");
    }
}
