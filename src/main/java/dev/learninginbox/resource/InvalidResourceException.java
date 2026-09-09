package dev.learninginbox.resource;

class InvalidResourceException extends RuntimeException {
    InvalidResourceException() {
        super("URL must be an absolute HTTP or HTTPS URL with a host and no credentials.");
    }
}
