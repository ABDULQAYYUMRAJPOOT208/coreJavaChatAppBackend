package org.example.exception;

/** Thrown when a duplicate resource is detected (e.g. email already exists). → 409 */
public class DuplicateResourceException extends AppException {
    public DuplicateResourceException(String message) {
        super(409, message);
    }
}
