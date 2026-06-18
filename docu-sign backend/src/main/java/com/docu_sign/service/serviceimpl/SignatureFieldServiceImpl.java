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
        validateFieldPlacement(document,request);

        SignatureField field = SignatureField.builder()
                .document(document)
                .signer(signer)
                .pageNumber(request.pageNumber())
                .xPosition(request.xPosition())
                .yPosition(request.yPosition())
                .width(request.width())
                .height(request.height())
                .required(request.required())
                .build();

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

        field.setXPosition( request.xPosition() );

        field.setYPosition( request.yPosition() );

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

    private void validateFieldPlacement(Document document, CreateSignatureFieldRequest request){
        try {
            byte[] pdfBytes = storageService.downloadFileBytes( document.getStoragePath());

            try (PDDocument pdf = Loader.loadPDF(pdfBytes)) {

                int totalPages = pdf.getNumberOfPages();

                if (request.pageNumber() > totalPages) {
                    throw new BusinessValidationException(
                            "Invalid page number"
                    );
                }

                PDRectangle mediaBox = pdf.getPage(request.pageNumber() - 1 ).getMediaBox();

                 float pageWidth = mediaBox.getWidth();
                 float pageHeight = mediaBox.getHeight();

                 float scaleX = pageWidth / 612.0f;
                 float scaleY = pageHeight / 792.0f;

                 float scaledX = request.xPosition() * scaleX;
                 float scaledWidth = request.width() * scaleX;
                 float scaledY = request.yPosition() * scaleY;
                 float scaledHeight = request.height() * scaleY;

                 if (scaledX + scaledWidth > pageWidth) {
                     throw new BusinessValidationException( "Field exceeds page width" );
                 }

                 if (scaledY + scaledHeight > pageHeight) {
                     throw new BusinessValidationException( "Field exceeds page height" );
                 }
            }
        } catch (BusinessValidationException ex) {

            throw ex;

        } catch (Exception ex) {

            throw new BusinessValidationException( "Unable to validate PDF placement" );
        }

    }




}
