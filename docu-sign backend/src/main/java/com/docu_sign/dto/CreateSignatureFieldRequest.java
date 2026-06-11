package com.docu_sign.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record CreateSignatureFieldRequest(

        @NotNull
        UUID signerId,

        @NotNull
        @Positive
        Integer pageNumber,

        @NotNull
        @PositiveOrZero
        Float xPosition,

        @NotNull
        @PositiveOrZero
        Float yPosition,

        @NotNull
        @Positive
        Float width,

        @NotNull
        @Positive
        Float height,

        @NotNull
        Boolean required

) {
}
