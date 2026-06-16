package com.docu_sign.service.serviceimpl;


import com.docu_sign.dto.FieldCompletionResponse;
import com.docu_sign.entity.FieldCompletion;
import com.docu_sign.entity.SignatureField;
import com.docu_sign.entity.Signer;
import com.docu_sign.exception.BusinessValidationException;
import com.docu_sign.exception.ResourceNotFoundException;
import com.docu_sign.repo.FieldCompletionRepository;
import com.docu_sign.repo.SignatureFieldRepository;
import com.docu_sign.repo.SignerRepository;
import com.docu_sign.service.FieldCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldCompletionServiceImpl implements FieldCompletionService {

    private final SignerRepository signerRepository;
    private final SignatureFieldRepository signatureFieldRepository;
    private final FieldCompletionRepository fieldCompletionRepository;

    @Override
    public FieldCompletionResponse completeField(String token, UUID fieldId) {
        Signer signer = signerRepository
                        .findBySigningToken(token)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Signing session not found"
                                )
                        );

        SignatureField field =
                signatureFieldRepository
                        .findById(fieldId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Signature field not found"
                                )
                        );

        if (!field.getSigner().getId().equals(signer.getId())) {
            throw new BusinessValidationException(
                    "Field does not belong to signer"
            );
        }

        if (fieldCompletionRepository
                .existsBySignatureField(field)) {

            throw new BusinessValidationException(
                    "Field already completed"
            );
        }

        if (signer.getSignatureImagePath() == null
                || signer.getSignatureImagePath().isBlank()) {

            throw new BusinessValidationException(
                    "Signer does not have a saved signature"
            );
        }

        FieldCompletion completion =
                FieldCompletion.builder()
                        .signatureField(field)
                        .signer(signer)
                        .build();

        completion =
                fieldCompletionRepository.save(
                        completion
                );

        return new FieldCompletionResponse(
                field.getId(),
                true,
                completion.getCompletedAt()
        );
    }
}
