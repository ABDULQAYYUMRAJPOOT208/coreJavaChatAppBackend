package org.example.exception;

/** Thrown when the caller does not have permission to access a resource. → 403 */
public class ForbiddenException extends AppException {
    public ForbiddenException(String message) {
        super(403, message);
    }
}
