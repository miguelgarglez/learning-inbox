package dev.learninginbox.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceService {
    private final ResourceRepository repository;

    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LearningResource create(UUID ownerId, CreateResourceRequest request) {
        validateUrl(request.url());
        // PostgreSQL timestamptz stores microsecond precision; align so POST and GET match.
        var resource = new LearningResource(
                UUID.randomUUID(),
                ownerId,
                request.title(),
                request.url(),
                request.reason(),
                LearningResource.Status.PENDING,
                Instant.now().truncatedTo(ChronoUnit.MICROS));
        repository.insert(resource);
        return resource;
    }

    @Transactional(readOnly = true)
    public LearningResource find(UUID ownerId, UUID id) {
        return repository.findByIdAndOwnerId(id, ownerId).orElseThrow(ResourceNotFoundException::new);
    }

    private void validateUrl(String value) {
        try {
            var uri = new URI(value);
            boolean http = "http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme());
            if (!http || uri.getHost() == null || uri.getRawUserInfo() != null) {
                throw new InvalidResourceException();
            }
        } catch (URISyntaxException exception) {
            throw new InvalidResourceException();
        }
    }
}
