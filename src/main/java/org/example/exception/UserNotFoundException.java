package org.example.exception;

/** Thrown when a user record cannot be found in the database. → 404 */
public class UserNotFoundException extends AppException {
    public UserNotFoundException(String message) {
        super(404, message);
    }
}
