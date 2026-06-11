package com.docu_sign.service;

import com.docu_sign.dto.PublicSignerViewResponse;

public interface PublicSigningService {

    PublicSignerViewResponse getSigningSession(String token);
}
