package com.docu_sign.controller;


import com.docu_sign.dto.CreateSignatureFieldRequest;
import com.docu_sign.dto.CreateSignatureFieldResponse;
import com.docu_sign.service.SignatureFieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignatureFieldController {

    private final SignatureFieldService signatureFieldService;

    @PostMapping("/documents/{documentId}/signature-fields")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSignatureFieldResponse createField( @PathVariable UUID documentId,
                                                     @Valid @RequestBody CreateSignatureFieldRequest request ) {
        return signatureFieldService.createField( documentId, request );
    }

    @GetMapping("/documents/{documentId}/signature-fields")
    public List<CreateSignatureFieldResponse> getFields( @PathVariable UUID documentId ) {
        return signatureFieldService.getFields( documentId );
    }

    @DeleteMapping("/signature-fields/{fieldId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteField( @PathVariable UUID fieldId ) {
        signatureFieldService.deleteField( fieldId );
    }


}
