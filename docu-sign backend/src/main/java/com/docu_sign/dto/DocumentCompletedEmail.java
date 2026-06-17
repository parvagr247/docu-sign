package com.docu_sign.dto;

public record DocumentCompletedEmail(
        String recipientName,
        String recipientEmail,
        String documentName
) {
}
