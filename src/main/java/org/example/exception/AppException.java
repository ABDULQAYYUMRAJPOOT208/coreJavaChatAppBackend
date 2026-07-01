package org.example.exception;

/**
 * Base application exception.
 * Every custom exception should extend this class and pass
 * the appropriate HTTP status code + a user-facing message.
 *
 * The GlobalExceptionHandler reads these two fields to build
 * the HTTP response automatically — you never need to write
 * try/catch in your controllers for business-logic errors.
 */
public class AppException extends RuntimeException {

    private final int statusCode;

    public AppException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
