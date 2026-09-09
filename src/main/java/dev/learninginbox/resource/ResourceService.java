package dev.learninginbox.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    private final ConcurrentMap<UUID, LearningResource> resources = new ConcurrentHashMap<>();

    public LearningResource create(CreateResourceRequest request) {
        validateUrl(request.url());
        var resource = new LearningResource(
                UUID.randomUUID(), request.title(), request.url(), request.reason(),
                LearningResource.Status.PENDING, Instant.now());
        resources.put(resource.id(), resource);
        return resource;
    }

    public LearningResource find(UUID id) {
        var resource = resources.get(id);
        if (resource == null) {
            throw new ResourceNotFoundException();
        }
        return resource;
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
