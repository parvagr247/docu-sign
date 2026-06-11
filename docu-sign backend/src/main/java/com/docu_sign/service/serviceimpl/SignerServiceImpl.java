package com.docu_sign.service.serviceimpl;


import com.docu_sign.dto.CreateSignerRequest;
import com.docu_sign.dto.SignerResponse;
import com.docu_sign.entity.Document;
import com.docu_sign.entity.Signer;
import com.docu_sign.entity.SignerStatus;
import com.docu_sign.entity.User;
import com.docu_sign.exception.DuplicateSignerException;
import com.docu_sign.exception.ResourceNotFoundException;
import com.docu_sign.repo.DocumentRepository;
import com.docu_sign.repo.SignerRepository;
import com.docu_sign.service.CurrentUserService;
import com.docu_sign.service.SignerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignerServiceImpl implements SignerService {

    private final SignerRepository signerRepository;
    private final DocumentRepository documentRepository;
    private final CurrentUserService currentUserService;

    @Override
    public SignerResponse createSigner(UUID documentId, CreateSignerRequest request) {

        Document document = getOwnedDocument(documentId);

        boolean signerAlreadyExists = signerRepository.existsByDocumentAndEmail( document, request.email());

        if (signerAlreadyExists) { throw new DuplicateSignerException( "Signer already exists for this document" );}

        Signer signer = Signer.builder()
                .document(document)
                .name(request.name())
                .email(request.email())
                .status(SignerStatus.PENDING)
                .signingToken(UUID.randomUUID().toString())
                .build();

        Signer savedSigner =
                signerRepository.save(signer);

        return mapToResponse(savedSigner);

    }

    @Override
    public List<SignerResponse> getDocumentSigners(UUID documentId) {

        Document document = getOwnedDocument(documentId);

        return signerRepository
                .findByDocument(document)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Document getOwnedDocument( UUID documentId ) {

        User currentUser = currentUserService.getCurrentUser();

        return documentRepository.findByIdAndUploadedBy( documentId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found") );
    }

    private SignerResponse mapToResponse( Signer signer ) {

        return new SignerResponse(
                signer.getId(),
                signer.getName(),
                signer.getEmail(),
                signer.getStatus(),
                signer.getCreatedAt()
        );
    }

}
