package com.example.cinema.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s с %s = '%s' не найден", resource, field, value));
    }
}
