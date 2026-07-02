package org.example.exception;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(String message) {
        super(404, message);
    }
}
