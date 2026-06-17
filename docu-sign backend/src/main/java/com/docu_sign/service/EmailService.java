package com.docu_sign.service;

import com.docu_sign.dto.DocumentCompletedEmail;
import com.docu_sign.dto.SignatureRequestEmail;

public interface EmailService {

        void sendSignatureRequestEmail( SignatureRequestEmail email );

        void sendDocumentCompletedEmail( DocumentCompletedEmail email );
}

