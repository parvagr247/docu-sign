package com.docu_sign.service;

import com.docu_sign.dto.SignatureRequestEmail;

public interface EmailService {

        void sendSignatureRequestEmail( SignatureRequestEmail email );
}

