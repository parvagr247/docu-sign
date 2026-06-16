package com.docu_sign.service;

import com.docu_sign.dto.FieldCompletionResponse;

import java.util.UUID;

public interface FieldCompletionService {

    FieldCompletionResponse completeField( String token,  UUID fieldId );
}
