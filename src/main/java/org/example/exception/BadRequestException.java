package org.example.exception;

/** Thrown when the incoming request body or parameters are invalid. → 400 */
public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
