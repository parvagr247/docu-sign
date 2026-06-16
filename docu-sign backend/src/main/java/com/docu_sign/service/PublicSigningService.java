package com.docu_sign.service;

import com.docu_sign.dto.PublicSignerViewResponse;
import com.docu_sign.dto.SubmitSignatureResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PublicSigningService {

    PublicSignerViewResponse getSigningSession(String token);

    SubmitSignatureResponse submitSignature(String token, MultipartFile signatureImage );

    byte[] getSignatureImage( String token );
}
