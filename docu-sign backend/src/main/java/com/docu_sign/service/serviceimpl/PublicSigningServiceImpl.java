package com.docu_sign.service.serviceimpl;

import com.docu_sign.dto.PublicSignerViewResponse;
import com.docu_sign.dto.SubmitSignatureResponse;
import com.docu_sign.entity.*;
import com.docu_sign.exception.BusinessValidationException;
import com.docu_sign.exception.ResourceNotFoundException;
import com.docu_sign.repo.DocumentRepository;
import com.docu_sign.repo.SignatureFieldRepository;
import com.docu_sign.repo.SignerRepository;
import com.docu_sign.service.PublicSigningService;
import com.docu_sign.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicSigningServiceImpl implements PublicSigningService {

    private final SignerRepository signerRepository;
    private final SignatureFieldRepository signatureFieldRepository;
    private final DocumentRepository documentRepository;
    private final StorageService storageService;

    @Override
    public PublicSignerViewResponse getSigningSession(String token) {
        Signer signer = signerRepository
                .findBySigningToken(token)
                .orElseThrow(() -> new ResourceNotFoundException( "Signing session not found" ));

        if (signer.getStatus() == SignerStatus.PENDING) {

            signer.setStatus(SignerStatus.VIEWED);
            signer.setViewedAt(LocalDateTime.now());

            signerRepository.save(signer);
        }

        Document document = signer.getDocument();

        return new PublicSignerViewResponse(
                signer.getId(),
                signer.getName(),
                signer.getEmail(),
                signer.getStatus(),
                document.getId(),
                document.getOriginalFileName(),
                document.getStatus()
        );

    }

    @Override
    public SubmitSignatureResponse submitSignature(String token, MultipartFile signatureImage) {
        System.out.println("ENTERED SUBMIT SIGNATURE");
        Signer signer = signerRepository
                .findBySigningToken(token)
                .orElseThrow( () -> new ResourceNotFoundException( "Signing session not found" ));

        if (signer.getStatus() == SignerStatus.SIGNED) {
            throw new BusinessValidationException( "Document already signed" );
        }

        if (signer.getStatus() == SignerStatus.REJECTED) {
            throw new BusinessValidationException( "Signing request already rejected" );
        }

        if (signer.getStatus() == SignerStatus.EXPIRED) {
            throw new BusinessValidationException( "Signing link expired" );
        }

        if (signatureImage.isEmpty()) {
            throw new BusinessValidationException( "Signature image is required" );
        }

        String contentType = signatureImage.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessValidationException( "Only image files are allowed" );
        }

        byte[] signatureBytes;

        try {
            signatureBytes = signatureImage.getBytes();
        } catch (Exception e) {
            throw new BusinessValidationException(
                    "Unable to read signature image"
            );
        }

        List<SignatureField> fields = signatureFieldRepository.findBySigner( signer );

        if (fields.isEmpty()) {
            throw new BusinessValidationException( "No signature fields configured" );
        }

        Document document = signer.getDocument();

        System.out.println("STEP 1 - Upload signature image");

        String signaturePath =
                storageService.uploadBytes(
                        signatureBytes,
                        "signatures/" + signer.getId() + ".png",
                        signatureImage.getContentType()
                );

        signer.setSignatureImagePath(signaturePath);

        System.out.println("STEP 2 - Download original PDF");

        byte[] originalPdfBytes =
                storageService.downloadFileBytes(
                        document.getStoragePath()
                );

        System.out.println("STEP 3 - Render signed PDF");

        byte[] signedPdfBytes =
                renderSignedPdf(
                        originalPdfBytes,
                        signatureBytes,
                        fields
                );

        System.out.println("STEP 4 - Upload signed PDF");

        String signedPdfPath =
                storageService.uploadBytes(
                        signedPdfBytes,
                        "signed-documents/" + document.getId() + ".pdf",
                        "application/pdf"
                );

        document.setSignedDocumentPath(
                signedPdfPath
        );

        signer.setStatus( SignerStatus.SIGNED );
        signer.setSignedAt( LocalDateTime.now() );

        boolean allSignersSigned =
                document.getSigners()
                        .stream()
                        .allMatch(
                                s -> s.getId().equals(signer.getId())
                                        || s.getStatus() == SignerStatus.SIGNED
                        );

        if (allSignersSigned) {

            document.setStatus(
                    DocumentStatus.SIGNED
            );

        } else {

            document.setStatus(
                    DocumentStatus.PARTIALLY_SIGNED
            );
        }

        System.out.println("STEP 5 - Save entities");

        signerRepository.save(signer);

        documentRepository.save(document);

        return new SubmitSignatureResponse(
                signer.getId(),
                signer.getStatus(),
                document.getStatus(),
                signer.getSignedAt(),
                "Document signed successfully"
        );

    }

    private byte[] renderSignedPdf( byte[] pdfBytes,  byte[] signatureBytes,  List<SignatureField> fields ) {

        try ( PDDocument pdf = Loader.loadPDF(pdfBytes) ) {
            PDImageXObject image = PDImageXObject.createFromByteArray( pdf, signatureBytes, "signature" );

            for (SignatureField field : fields) {

                PDPage page = pdf.getPage(field.getPageNumber() - 1 );

                try (
                        PDPageContentStream contentStream = new PDPageContentStream( pdf, page, PDPageContentStream.AppendMode.APPEND, true )
                ) {
                    contentStream.drawImage(
                            image,
                            field.getXPosition(),
                            field.getYPosition(),
                            field.getWidth(),
                            field.getHeight()
                    );
                }
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            pdf.save(output);

            return output.toByteArray();

        } catch (Exception ex) {

            ex.printStackTrace();

            throw new BusinessValidationException(
                    "Unable to generate signed PDF : "
                            + ex.getMessage()
            );
        }

    }
}
