package dev.learninginbox.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateResourceRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 2048) String url,
        @Size(max = 1000) String reason) {

    public CreateResourceRequest {
        // Normalizar antes de validar el límite del título.
        title = title == null ? null : title.strip();
    }
}
