package com.docu_sign.controller;

import com.docu_sign.dto.PublicSignerViewResponse;
import com.docu_sign.service.PublicSigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/sign")
@RequiredArgsConstructor
public class PublicSigningController {

    private final PublicSigningService publicSigningService;

    @GetMapping("/{token}")
    public PublicSignerViewResponse getSigningSession( @PathVariable String token ) {
        return publicSigningService.getSigningSession(token);
    }

}
