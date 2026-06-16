package com.docu_sign.dto;

import java.util.UUID;

public record SaveSignatureResponse(
        UUID signerId,

       String signatureImagePath,

       String message) {
}
