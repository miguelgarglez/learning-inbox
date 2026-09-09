package dev.learninginbox.resource;

class ResourceNotFoundException extends RuntimeException {
    ResourceNotFoundException() {
        super("Resource not found.");
    }
}
