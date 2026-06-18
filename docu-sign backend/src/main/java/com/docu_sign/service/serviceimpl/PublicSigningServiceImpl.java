package com.docu_sign.service.serviceimpl;

import com.docu_sign.dto.*;
import com.docu_sign.entity.*;
import com.docu_sign.exception.BusinessValidationException;
import com.docu_sign.exception.ResourceNotFoundException;
import com.docu_sign.repo.DocumentRepository;
import com.docu_sign.repo.FieldCompletionRepository;
import com.docu_sign.repo.SignatureFieldRepository;
import com.docu_sign.repo.SignerRepository;
import com.docu_sign.service.*;
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
    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    private final AuditLogService auditLogService;
    private final SignatureFieldRepository signatureFieldRepository;
    private final FieldCompletionRepository fieldCompletionRepository;
    private final CertificateService certificateService;
    private final EmailService emailService;

    @Override
    public PublicSignerViewResponse getSigningSession(String token) {
        Signer signer = signerRepository
                .findBySigningToken(token)
                .orElseThrow(() -> new ResourceNotFoundException( "Signing session not found" ));

        if (signer.getStatus() == SignerStatus.PENDING) {

            signer.setStatus(SignerStatus.VIEWED);
            signer.setViewedAt(LocalDateTime.now());

            signerRepository.save(signer);

            auditLogService.logEvent(
                    signer.getDocument(),
                    signer,
                    AuditEventType.LINK_OPENED,
                    signer.getEmail()
            );
        }

        Document document = signer.getDocument();

        List<SignatureField> fields =
                signatureFieldRepository.findBySigner(signer);

        List<SignatureFieldViewResponse> fieldResponses =
                fields.stream()
                        .map(field -> new SignatureFieldViewResponse(

                                field.getId(),
                                field.getPageNumber(),
                                field.getXPosition(),
                                field.getYPosition(),
                                field.getWidth(),
                                field.getHeight(),
                                field.getRequired(),
                                fieldCompletionRepository
                                        .existsBySignatureField(field)
                        ))
                        .toList();

        return new PublicSignerViewResponse(
                signer.getId(),
                signer.getName(),
                signer.getEmail(),
                signer.getStatus(),
                document.getId(),
                document.getOriginalFileName(),
                document.getStatus(),
                signer.getSignatureImagePath(),
                fieldResponses
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

        String pdfStoragePath = document.getSignedDocumentPath() != null ?
                document.getSignedDocumentPath() :
                document.getStoragePath();

        byte[] originalPdfBytes =
                storageService.downloadFileBytes(
                        pdfStoragePath
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

        auditLogService.logEvent(
                document,
                signer,
                AuditEventType.DOCUMENT_SIGNED,
                signer.getEmail()
        );

        return new SubmitSignatureResponse(
                signer.getId(),
                signer.getStatus(),
                document.getStatus(),
                signer.getSignedAt(),
                "Document signed successfully"
        );

    }

    @Override
    public byte[] getSignatureImage(
            String token
    ) {

        Signer signer =
                signerRepository
                        .findBySigningToken(token)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Signing session not found"
                                        )
                        );

        if (
                signer.getSignatureImagePath()
                        == null
        ) {

            throw new BusinessValidationException(
                    "No signature found"
            );
        }

        return storageService
                .downloadFileBytes(
                        signer.getSignatureImagePath()
                );
    }

    @Override
    public byte[] downloadDocument(
            String token
    ) {

        Signer signer =
                signerRepository
                        .findBySigningToken(token)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Signing session not found"
                                        )
                        );

        String path = signer.getDocument().getSignedDocumentPath() != null ?
                signer.getDocument().getSignedDocumentPath() :
                signer.getDocument().getStoragePath();

        return storageService
                .downloadFileBytes(
                        path
                );
    }

    @Override
    public SaveSignatureResponse saveSignature(
            String token,
            MultipartFile signatureImage
    ) {

        Signer signer =
                signerRepository
                        .findBySigningToken(token)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Signing session not found"
                                        )
                        );

        if (signatureImage.isEmpty()) {

            throw new BusinessValidationException(
                    "Signature image is required"
            );
        }

        String contentType =
                signatureImage.getContentType();

        if (
                contentType == null ||
                        !contentType.startsWith("image/")
        ) {

            throw new BusinessValidationException(
                    "Only image files are allowed"
            );
        }

        byte[] signatureBytes;

        try {

            signatureBytes =
                    signatureImage.getBytes();

        } catch (Exception ex) {

            throw new BusinessValidationException(
                    "Unable to read signature image"
            );
        }

        String signaturePath =
                storageService.uploadBytes(
                        signatureBytes,
                        "signatures/"
                                + signer.getId()
                                + ".png",
                        contentType
                );

        signer.setSignatureImagePath(
                signaturePath
        );

        signerRepository.save(
                signer
        );

        return new SaveSignatureResponse(
                signer.getId(),
                signaturePath,
                "Signature saved successfully"
        );
    }

    @Deprecated
    @Override
    public CompleteSigningResponse completeSigning(
            String token
    ) {

        System.out.println("COMPLETE SIGNING HIT");

        Signer signer =
                signerRepository
                        .findBySigningToken(token)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Signing session not found"
                                        )
                        );

        if (
                signer.getSignatureImagePath()
                        == null
        ) {

            throw new BusinessValidationException(
                    "Please save a signature first"
            );
        }

        List<SignatureField> fields =
                signatureFieldRepository
                        .findBySigner(signer);

        for (SignatureField field : fields) {

            if (
                    field.getRequired()
                            &&
                            !fieldCompletionRepository
                                    .existsBySignatureField(field)
            ) {

                throw new BusinessValidationException(
                        "Please complete all required fields"
                );
            }
        }

        if (
                signer.getStatus()
                        == SignerStatus.SIGNED
        ) {

            throw new BusinessValidationException(
                    "Document already signed"
            );
        }

        signer.setStatus(SignerStatus.SIGNED);
        signer.setSignedAt(LocalDateTime.now());

        Document document = signer.getDocument();

        System.out.println("STEP A - START PDF GENERATION");

        byte[] signatureBytes =
                storageService.downloadFileBytes(
                        signer.getSignatureImagePath()
                );

        System.out.println("STEP B - PDF DOWNLOADED");

        String pdfStoragePath = document.getSignedDocumentPath() != null ?
                document.getSignedDocumentPath() :
                document.getStoragePath();

        byte[] originalPdfBytes =
                storageService.downloadFileBytes(
                        pdfStoragePath
                );

        System.out.println("STEP C - PDF RENDERED");


        byte[] signedPdfBytes =
                renderSignedPdf(
                        originalPdfBytes,
                        signatureBytes,
                        fields
                );



        String signedPdfPath =
                storageService.uploadBytes(
                        signedPdfBytes,
                        "signed-documents/"
                                + document.getId()
                                + ".pdf",
                        "application/pdf"
                );

        document.setSignedDocumentPath(
                signedPdfPath
        );

        System.out.println("STEP D - SIGNED PDF SAVED");
        System.out.println(
                "SIGNED PDF PATH = " + signedPdfPath
        );

        boolean allSignersSigned =
                document.getSigners()
                        .stream()
                        .allMatch(
                                s ->
                                        s.getId().equals(
                                                signer.getId()
                                        )
                                                ||
                                                s.getStatus()
                                                        ==
                                                        SignerStatus.SIGNED
                        );

        if (allSignersSigned) {

            System.out.println("ALL SIGNERS SIGNED");

            document.setStatus(
                    DocumentStatus.SIGNED
            );

            String certificatePath =
                    certificateService
                            .generateCertificate(
                                    document
                            );

            document.setCertificatePath(
                    certificatePath
            );

            DocumentCompletedEmail email =
                    new DocumentCompletedEmail(

                            document
                                    .getUploadedBy()
                                    .getFullName(),

                            document
                                    .getUploadedBy()
                                    .getEmail(),

                            document
                                    .getOriginalFileName()

                    );

            System.out.println("ABOUT TO SEND COMPLETION EMAIL");

            emailService
                    .sendDocumentCompletedEmail(
                            email
                    );
        } else {

            document.setStatus(
                    DocumentStatus.PARTIALLY_SIGNED
            );
        }

        System.out.println(
                "STATUS BEFORE SAVE = "
                        + signer.getStatus()
        );

        System.out.println(
                "SIGNED_AT BEFORE SAVE = "
                        + signer.getSignedAt()
        );

        signerRepository.save(
                signer
        );

        documentRepository.save(
                document
        );

        auditLogService.logEvent(
                document,
                signer,
                AuditEventType.DOCUMENT_SIGNED,
                signer.getEmail()
        );

        return new CompleteSigningResponse(

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

                float pageHeight =
                        page.getMediaBox().getHeight();
                float pageWidth =
                        page.getMediaBox().getWidth();

                float scaleX = pageWidth / 612.0f;
                float scaleY = pageHeight / 792.0f;

                float pdfX = field.getXPosition() * scaleX;
                float pdfY = field.getYPosition() * scaleY;
                float width = field.getWidth() * scaleX;
                float height = field.getHeight() * scaleY;

                try (
                        PDPageContentStream contentStream =
                                new PDPageContentStream(
                                        pdf,
                                        page,
                                        PDPageContentStream.AppendMode.APPEND,
                                        true
                                )
                ) {

                    contentStream.drawImage(
                            image,
                            pdfX,
                            pdfY,
                            width,
                            height
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
