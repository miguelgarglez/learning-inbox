package dev.learninginbox.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
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
    public LearningResource create(CreateResourceRequest request) {
        validateUrl(request.url());
        var resource = new LearningResource(
                UUID.randomUUID(), request.title(), request.url(), request.reason(),
                LearningResource.Status.PENDING, Instant.now());
        repository.insert(resource);
        return resource;
    }

    @Transactional(readOnly = true)
    public LearningResource find(UUID id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
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
