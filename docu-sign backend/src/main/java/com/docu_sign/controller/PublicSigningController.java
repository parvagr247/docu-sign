package com.docu_sign.controller;

import com.docu_sign.dto.FieldCompletionResponse;
import com.docu_sign.dto.PublicSignerViewResponse;
import com.docu_sign.dto.SubmitSignatureResponse;
import com.docu_sign.service.FieldCompletionService;
import com.docu_sign.service.PublicSigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/sign")
@RequiredArgsConstructor
public class PublicSigningController {

    private final PublicSigningService publicSigningService;
    private final FieldCompletionService fieldCompletionService;

    @GetMapping("/{token}")
    public PublicSignerViewResponse getSigningSession( @PathVariable String token ) {
        return publicSigningService.getSigningSession(token);
    }

    @PostMapping("/{token}")
    public SubmitSignatureResponse submitSignature( @PathVariable String token, @RequestParam("signatureImage") MultipartFile signatureImage ) {

        System.out.println("CONTROLLER HIT");
        return publicSigningService.submitSignature( token,  signatureImage );
    }

    @PostMapping( "/{token}/fields/{fieldId}/complete" )
    public FieldCompletionResponse completeField( @PathVariable String token,  @PathVariable UUID fieldId
    ) {
        return fieldCompletionService.completeField( token, fieldId );
    }

    @GetMapping( value = "/{token}/signature", produces = MediaType.IMAGE_PNG_VALUE )
    public byte[] getSignatureImage( @PathVariable String token ) {

        return publicSigningService.getSignatureImage(token);
    }

}
