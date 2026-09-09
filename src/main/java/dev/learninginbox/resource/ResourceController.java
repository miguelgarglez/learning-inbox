package dev.learninginbox.resource;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<LearningResource> create(@Valid @RequestBody CreateResourceRequest request) {
        var resource = service.create(request);
        return ResponseEntity.created(URI.create("/api/resources/" + resource.id())).body(resource);
    }

    @GetMapping("/{id}")
    public LearningResource find(@PathVariable UUID id) {
        return service.find(id);
    }
}
