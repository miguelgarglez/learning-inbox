package dev.learninginbox.resource;

import java.time.Instant;
import java.util.UUID;

public record LearningResource(
        UUID id, String title, String url, String reason, Status status, Instant createdAt) {
    public enum Status {
        PENDING
    }
}
