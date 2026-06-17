package com.docu_sign.service;

import com.docu_sign.entity.Document;

public interface CertificateService {

    String generateCertificate(Document document);
}
