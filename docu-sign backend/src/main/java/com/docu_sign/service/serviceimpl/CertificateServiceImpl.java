package com.docu_sign.service.serviceimpl;

import com.docu_sign.entity.Document;
import com.docu_sign.entity.Signer;
import com.docu_sign.exception.BusinessValidationException;
import com.docu_sign.service.CertificateService;
import com.docu_sign.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final StorageService storageService;

    @Override
    public String generateCertificate( Document document ) {

        try (
                PDDocument pdf = new PDDocument()
        ) {

            PDPage page = new PDPage();
            pdf.addPage(page);

            PDPageContentStream content = new PDPageContentStream(pdf, page);

            content.beginText();

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA
                    ),
                    12
            );

            content.setLeading(18);

            content.newLineAtOffset(
                    50,
                    700
            );
            content.showText( "Certificate Of Completion");
            content.newLine();
            content.newLine();
            content.showText("Document: " + document.getOriginalFileName());

            content.newLine();

            content.showText( "Document ID: " + document.getId());
            content.newLine();

            content.showText("Completed At: " + LocalDateTime.now());

            content.newLine();
            content.newLine();

            content.showText( "Signers:");

            for ( Signer signer : document.getSigners()) {

                content.newLine();

                content.showText( signer.getName() + " - " + signer.getEmail());
            }

            content.endText();

            content.close();

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            pdf.save(output);

            String certificatePath =
                    "certificates/"
                            + document.getId()
                            + ".pdf";

            storageService.uploadBytes(
                    output.toByteArray(),
                    certificatePath,
                    "application/pdf"
            );

            return certificatePath;

        } catch (Exception ex) {

            ex.printStackTrace();

            throw new BusinessValidationException(
                    "Unable to generate certificate : "
                            + ex.getMessage()
            );
        }
    }
}