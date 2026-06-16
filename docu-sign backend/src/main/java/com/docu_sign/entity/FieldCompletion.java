package com.docu_sign.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "field_completions",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "signature_field_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "signature_field_id",
            nullable = false
    )
    private SignatureField signatureField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "signer_id",
            nullable = false
    )
    private Signer signer;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        this.completedAt = LocalDateTime.now();
    }
}