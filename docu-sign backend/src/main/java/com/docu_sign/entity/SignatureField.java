package com.docu_sign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "signature_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatureField {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signer_id", nullable = false)
    private Signer signer;

    @Column(nullable = false)
    private Integer pageNumber;

    @Column(nullable = false)
    private Float xPosition;

    @Column(nullable = false)
    private Float yPosition;

    @Column(nullable = false)
    private Float width;

    @Column(nullable = false)
    private Float height;

    @Column(nullable = false)
    private Boolean required;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}

