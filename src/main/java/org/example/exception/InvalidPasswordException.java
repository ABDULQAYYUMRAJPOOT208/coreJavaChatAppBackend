package org.example.exception;

/** Thrown when the supplied password does not match the stored hash. → 401 */
public class InvalidPasswordException extends AppException {
    public InvalidPasswordException(String message) {
        super(401, message);
    }
}
