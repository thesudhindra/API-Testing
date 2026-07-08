package com.playground.enterprise.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDocumentRequest(
        @NotBlank String partyId,
        @NotBlank @Size(max = 64) String documentType,
        @NotBlank @Size(max = 256) String fileName,
        @NotBlank @Size(max = 128) String contentType
) {
}
