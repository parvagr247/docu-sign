package com.docu_sign.service.serviceimpl;

import com.docu_sign.dto.PublicSignerViewResponse;
import com.docu_sign.entity.Document;
import com.docu_sign.entity.Signer;
import com.docu_sign.entity.SignerStatus;
import com.docu_sign.exception.ResourceNotFoundException;
import com.docu_sign.repo.SignerRepository;
import com.docu_sign.service.PublicSigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PublicSignerServiceImpl implements PublicSigningService {

    private final SignerRepository signerRepository;

    @Override
    public PublicSignerViewResponse getSigningSession(String token) {
        Signer signer = signerRepository
                .findBySigningToken(token)
                .orElseThrow(() -> new ResourceNotFoundException( "Signing session not found" ));

        if (signer.getStatus() == SignerStatus.PENDING) {

            signer.setStatus(SignerStatus.VIEWED);
            signer.setViewedAt(LocalDateTime.now());

            signerRepository.save(signer);
        }

        Document document = signer.getDocument();

        return new PublicSignerViewResponse(
                signer.getId(),
                signer.getName(),
                signer.getEmail(),
                signer.getStatus(),
                document.getId(),
                document.getOriginalFileName(),
                document.getStatus()
        );

    }
}
