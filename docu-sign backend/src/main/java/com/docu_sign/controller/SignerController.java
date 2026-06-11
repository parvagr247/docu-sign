package com.docu_sign.controller;

import com.docu_sign.dto.CreateSignerRequest;
import com.docu_sign.dto.SignerResponse;
import com.docu_sign.service.SignerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class SignerController {

    private final SignerService signerService;

    @PostMapping("/{documentId}/signers")
    @ResponseStatus(HttpStatus.CREATED)
    public SignerResponse createSigner( @PathVariable UUID documentId, @Valid @RequestBody CreateSignerRequest request ) {
        return signerService.createSigner( documentId, request );
    }

    @GetMapping("/{documentId}/signers")
    public List<SignerResponse> getDocumentSigners( @PathVariable UUID documentId ) {
        return signerService.getDocumentSigners( documentId );
    }

}
