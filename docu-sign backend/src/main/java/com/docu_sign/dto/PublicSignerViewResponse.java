package com.docu_sign.dto;

import com.docu_sign.entity.DocumentStatus;
import com.docu_sign.entity.SignerStatus;

import java.util.List;
import java.util.UUID;

public record PublicSignerViewResponse(

        UUID signerId,

        String signerName,

        String signerEmail,

        SignerStatus signerStatus,

        UUID documentId,

        String documentName,

        DocumentStatus documentStatus,

        String signatureImagePath,

        List<SignatureFieldViewResponse> fields



) {
}
