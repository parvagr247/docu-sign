package com.docu_sign.controller;

import com.docu_sign.dto.PublicSignerViewResponse;
import com.docu_sign.dto.SubmitSignatureResponse;
import com.docu_sign.service.PublicSigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/public/sign")
@RequiredArgsConstructor
public class PublicSigningController {

    private final PublicSigningService publicSigningService;

    @GetMapping("/{token}")
    public PublicSignerViewResponse getSigningSession( @PathVariable String token ) {
        return publicSigningService.getSigningSession(token);
    }

    @PostMapping("/{token}")
    public SubmitSignatureResponse submitSignature( @PathVariable String token, @RequestParam("signatureImage") MultipartFile signatureImage ) {

        System.out.println("CONTROLLER HIT");
        return publicSigningService.submitSignature( token,  signatureImage );
    }

}
