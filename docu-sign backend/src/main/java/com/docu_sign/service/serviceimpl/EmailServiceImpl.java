package com.docu_sign.service.serviceimpl;

import com.docu_sign.component.EmailTemplateBuilder;
import com.docu_sign.dto.DocumentCompletedEmail;
import com.docu_sign.dto.SignatureRequestEmail;
import com.docu_sign.exception.EmailDeliveryException;
import com.docu_sign.service.EmailService;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailTemplateBuilder emailTemplateBuilder;

    @Value("${resend.api.key}")
    private String apiKey;

    private final OkHttpClient client =
            new OkHttpClient();

    @Override
    public void sendSignatureRequestEmail(
            SignatureRequestEmail email
    ) {

        try {

            String htmlContent =
                    emailTemplateBuilder
                            .buildSignatureRequestEmail(
                                    email.recipientName(),
                                    email.documentName(),
                                    email.signingUrl()
                            );

            sendEmail(
                    email.recipientEmail(),
                    "Signature Requested: "
                            + email.documentName(),
                    htmlContent
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            throw new EmailDeliveryException(
                    "Failed to send email to "
                            + email.recipientEmail(),
                    ex
            );
        }
    }

    @Override
    public void sendDocumentCompletedEmail(
            DocumentCompletedEmail email
    ) {

        try {

            String htmlContent =
                    emailTemplateBuilder
                            .buildDocumentCompletedEmail(
                                    email.recipientName(),
                                    email.documentName()
                            );

            sendEmail(
                    email.recipientEmail(),
                    "Document Completed: "
                            + email.documentName(),
                    htmlContent
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            throw new EmailDeliveryException(
                    "Failed to send completion email",
                    ex
            );
        }
    }

    private void sendEmail(
            String recipientEmail,
            String subject,
            String htmlContent
    ) throws IOException {

        String escapedHtml =
                htmlContent
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "")
                        .replace("\r", "");

        String json =
                """
                {
                  "from":"DocuSign <onboarding@resend.dev>",
                  "to":["%s"],
                  "subject":"%s",
                  "html":"%s"
                }
                """
                        .formatted(
                                recipientEmail,
                                subject,
                                escapedHtml
                        );

        RequestBody body =
                RequestBody.create(
                        json,
                        MediaType.parse(
                                "application/json"
                        )
                );

        Request request =
                new Request.Builder()
                        .url(
                                "https://api.resend.com/emails"
                        )
                        .addHeader(
                                "Authorization",
                                "Bearer " + apiKey
                        )
                        .addHeader(
                                "Content-Type",
                                "application/json"
                        )
                        .post(body)
                        .build();

        try (
                Response response =
                        client.newCall(request)
                                .execute()
        ) {

            String responseBody =
                    response.body() != null
                            ? response.body().string()
                            : "";

            System.out.println(
                    "RESEND STATUS = "
                            + response.code()
            );

            System.out.println(
                    "RESEND RESPONSE = "
                            + responseBody
            );

            if (!response.isSuccessful()) {

                throw new RuntimeException(
                        "Resend API Error: "
                                + responseBody
                );
            }
        }
    }
}