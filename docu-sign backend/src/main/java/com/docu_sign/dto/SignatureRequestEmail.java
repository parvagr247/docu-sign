package com.docu_sign.dto;

public record SignatureRequestEmail(
        String recipientName,
        String recipientEmail,
        String documentName,
        String signingUrl
) {
}