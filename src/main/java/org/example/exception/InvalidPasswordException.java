package org.example.exception;

public class InvalidPasswordException extends AppException {
    public InvalidPasswordException(String message) {
        super(401, message);
    }
}
