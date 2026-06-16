package com.docu_sign.service;

import com.docu_sign.dto.CompleteSigningResponse;
import com.docu_sign.dto.PublicSignerViewResponse;
import com.docu_sign.dto.SaveSignatureResponse;
import com.docu_sign.dto.SubmitSignatureResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PublicSigningService {

    PublicSignerViewResponse getSigningSession(String token);

    SubmitSignatureResponse submitSignature(String token, MultipartFile signatureImage );

    byte[] getSignatureImage( String token );

    byte[] downloadDocument(String token);

    SaveSignatureResponse saveSignature( String token, MultipartFile signatureImage );

    CompleteSigningResponse completeSigning( String token );
}
