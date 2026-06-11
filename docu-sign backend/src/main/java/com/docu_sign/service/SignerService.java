package com.docu_sign.service;

import com.docu_sign.dto.CreateSignerRequest;
import com.docu_sign.dto.SignerResponse;

import java.util.List;
import java.util.UUID;

public interface SignerService {

    SignerResponse createSigner( UUID documentId, CreateSignerRequest request);

    List<SignerResponse> getDocumentSigners( UUID documentId );
}
