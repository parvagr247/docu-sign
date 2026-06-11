package com.docu_sign.service;

import com.docu_sign.dto.CreateSignatureFieldRequest;
import com.docu_sign.dto.CreateSignatureFieldResponse;

import java.util.List;
import java.util.UUID;

public interface SignatureFieldService {

    CreateSignatureFieldResponse createField( UUID documentId, CreateSignatureFieldRequest request );

    List<CreateSignatureFieldResponse> getFields(UUID documentId );

    void deleteField(UUID fieldId);
}
