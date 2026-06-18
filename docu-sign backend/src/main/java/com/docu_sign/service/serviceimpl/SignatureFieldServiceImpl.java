package com.docu_sign.service.serviceimpl;

import com.docu_sign.dto.CreateSignatureFieldRequest;
import com.docu_sign.dto.CreateSignatureFieldResponse;
import com.docu_sign.dto.UpdateSignatureFieldRequest;
import com.docu_sign.entity.*;
import com.docu_sign.exception.BusinessValidationException;
import com.docu_sign.exception.ResourceNotFoundException;
import com.docu_sign.repo.DocumentRepository;
import com.docu_sign.repo.SignatureFieldRepository;
import com.docu_sign.repo.SignerRepository;
import com.docu_sign.service.AuditLogService;
import com.docu_sign.service.CurrentUserService;
import com.docu_sign.service.SignatureFieldService;
import com.docu_sign.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignatureFieldServiceImpl implements SignatureFieldService {

    private final SignatureFieldRepository signatureFieldRepository;
    private final DocumentRepository documentRepository;
    private final SignerRepository signerRepository;
    private final CurrentUserService currentUserService;
    private final StorageService storageService;
    private final AuditLogService auditLogService;

    @Override
    public CreateSignatureFieldResponse createField(UUID documentId, CreateSignatureFieldRequest request) {
        Document document = getOwnedDocument(documentId);

        Signer signer = signerRepository.findById(request.signerId())
                .orElseThrow(() -> new ResourceNotFoundException(("Signer Not Found")));

        validateSignerBelongsToDocument(signer, document);

        SignatureField field = SignatureField.builder()
                .document(document)
                .signer(signer)
                .pageNumber(request.pageNumber())
                .width(request.width())
                .height(request.height())
                .required(request.required())
                .build();

        clampAndSetCoordinates(field, document, request.xPosition(), request.yPosition());

        SignatureField savedField = signatureFieldRepository.save(field);

        auditLogService.logEvent(
                document,
                signer,
                AuditEventType.SIGNATURE_FIELD_CREATED,
                "Page " + savedField.getPageNumber()
        );

        return mapToResponse(savedField);
    }

    @Override
    public List<CreateSignatureFieldResponse> getFields(UUID documentId) {
        Document document = getOwnedDocument(documentId);

        return signatureFieldRepository
                .findByDocument(document)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CreateSignatureFieldResponse updateField(UUID fieldId, UpdateSignatureFieldRequest request) {
        SignatureField field =
                signatureFieldRepository
                        .findById(fieldId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException( "Signature field not found" ));

        Document document = field.getDocument();

        getOwnedDocument( document.getId() );

        clampAndSetCoordinates(field, document, request.xPosition(), request.yPosition());

        SignatureField updatedField = signatureFieldRepository.save(field);

        return mapToResponse(updatedField);
    }

    @Override
    public void deleteField(UUID fieldId) {

        SignatureField field = signatureFieldRepository
                .findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException(("Signature Field not found")));

        Document document = field.getDocument();

        getOwnedDocument(document.getId());

        signatureFieldRepository.delete(field);

    }

    private Document getOwnedDocument(UUID documentId) {
        User currentUser = currentUserService.getCurrentUser();
        return documentRepository
                .findByIdAndUploadedBy( documentId, currentUser )
                .orElseThrow(() -> new ResourceNotFoundException( "Document not found" ));
    }

    private void validateSignerBelongsToDocument( Signer signer, Document document ) {
        if (!signer.getDocument().getId()
                .equals(document.getId())) {
            throw new BusinessValidationException( "Signer does not belong to document" );
        }
    }

    private CreateSignatureFieldResponse mapToResponse( SignatureField field ) {

        return CreateSignatureFieldResponse.builder()
                .id(field.getId())
                .signerId(field.getSigner().getId())
                .pageNumber(field.getPageNumber())
                .xPosition(field.getXPosition())
                .yPosition(field.getYPosition())
                .width(field.getWidth())
                .height(field.getHeight())
                .required(field.getRequired())
                .createdAt(field.getCreatedAt())
                .build();
    }

    private void clampAndSetCoordinates(SignatureField field, Document document, float x, float y) {
        try {
            byte[] pdfBytes = storageService.downloadFileBytes(document.getStoragePath());

            try (PDDocument pdf = Loader.loadPDF(pdfBytes)) {
                int totalPages = pdf.getNumberOfPages();

                if (field.getPageNumber() > totalPages) {
                    throw new BusinessValidationException("Invalid page number");
                }

                PDPage page = pdf.getPage(field.getPageNumber() - 1);
                PDRectangle cropBox = page.getCropBox();
                int rotation = page.getRotation();

                float pageWidth = cropBox.getWidth();
                float pageHeight = cropBox.getHeight();
                if (rotation == 90 || rotation == 270) {
                    pageWidth = cropBox.getHeight();
                    pageHeight = cropBox.getWidth();
                }

                // Clamp to page boundaries
                float clampedX = Math.max(0f, Math.min(pageWidth - field.getWidth(), x));
                float clampedY = Math.max(0f, Math.min(pageHeight - field.getHeight(), y));

                field.setXPosition(clampedX);
                field.setYPosition(clampedY);
            }
        } catch (BusinessValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Fallback if PDF loading fails
            field.setXPosition(Math.max(0f, x));
            field.setYPosition(Math.max(0f, y));
        }
    }




}
