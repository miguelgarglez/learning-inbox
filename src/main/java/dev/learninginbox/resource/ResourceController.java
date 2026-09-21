package dev.learninginbox.resource;

import dev.learninginbox.security.AuthenticatedUser;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    private final ResourceService service;

    public ResourceController(ResourceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LearningResource> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateResourceRequest request) {
        var resource = service.create(user.id(), request);
        return ResponseEntity.created(URI.create("/api/resources/" + resource.id())).body(resource);
    }

    @GetMapping("/{id}")
    public LearningResource find(
            @AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        return service.find(user.id(), id);
    }
}
