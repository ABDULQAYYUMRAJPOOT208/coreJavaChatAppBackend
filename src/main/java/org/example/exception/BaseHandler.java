package org.example.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ──────────────────────────────────────────────────────────────
 *  GLOBAL EXCEPTION HANDLER
 * ──────────────────────────────────────────────────────────────
 *
 * Every controller must extend this class instead of
 * implementing HttpHandler directly.
 *
 * HOW IT WORKS
 * ─────────────
 * 1.  Your controller overrides  handleRequest()  (not handle()).
 * 2.  This class calls handleRequest() inside a try/catch block.
 * 3.  If your service / repo throws any AppException subclass
 *     (UserNotFoundException, InvalidPasswordException, …) the
 *     handler reads the status code & message automatically and
 *     returns a clean JSON error to the frontend.
 * 4.  Any unexpected exception becomes a 500 Internal Server Error.
 *
 * HOW TO USE IN YOUR SERVICE / REPO
 * ───────────────────────────────────
 *   // User not found → just throw, no try/catch needed in controller
 *   throw new UserNotFoundException("User with email " + email + " not found");
 *
 *   // Wrong password
 *   throw new InvalidPasswordException("Incorrect password");
 *
 *   // Duplicate email on sign-up
 *   throw new DuplicateResourceException("Email already registered");
 *
 * The frontend will receive:
 *   HTTP 404  { "status": 404, "error": "User with email … not found" }
 *   HTTP 401  { "status": 401, "error": "Incorrect password" }
 *   HTTP 409  { "status": 409, "error": "Email already registered" }
 */
public abstract class BaseHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(BaseHandler.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private boolean responseSent = false;

    // ── Entry point called by the HTTP server ──────────────────
    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        try {
            handleRequest(exchange);
        } catch (AppException ex) {
            // Known business-logic error — send its own status + message
            LOGGER.log(Level.WARNING, "AppException [{0}]: {1}",
                    new Object[]{ex.getStatusCode(), ex.getMessage()});
            if (!responseSent) {
                sendErrorResponse(exchange, ex.getStatusCode(), ex.getMessage());
            }

        } catch (Exception ex) {
            // Unexpected / unhandled error — always 500
            LOGGER.log(Level.SEVERE, "Unhandled exception in " + getClass().getSimpleName(), ex);
            if (!responseSent) {
                sendErrorResponse(exchange, 500, "Internal server error. Please try again later.");
            }
        } finally {
            exchange.close();
        }
    }

    /**
     * Override this in every controller instead of handle().
     * You can throw any AppException subclass freely — the global
     * handler above will catch it and send the right response.
     */
    protected abstract void handleRequest(HttpExchange exchange) throws Exception;

    // ── Helpers available to all controllers ──────────────────

    /**
     * Send a successful JSON response.
     *
     * @param exchange   the current HTTP exchange
     * @param statusCode HTTP status (e.g. 200, 201)
     * @param body       any object — will be serialised to JSON automatically
     */
    protected void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        if (statusCode == 204) { // 204 No Content must not have a body
            exchange.sendResponseHeaders(statusCode, -1); // -1 indicates no response body
            responseSent = true;
            return;
        }

        String json = OBJECT_MAPPER.writeValueAsString(body);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = json.getBytes();
        exchange.sendResponseHeaders(statusCode, bytes.length);
        responseSent = true;
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Send a plain-text response.
     */
    protected void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        if (statusCode == 204) { // 204 No Content must not have a body
            exchange.sendResponseHeaders(statusCode, -1); // -1 indicates no response body
            responseSent = true;
            return;
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        byte[] bytes = text.getBytes();
        exchange.sendResponseHeaders(statusCode, bytes.length);
        responseSent = true;
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // ── Private helpers ────────────────────────────────────────

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) {
        try {
            // Return a structured JSON error so the frontend can parse it easily
            String json = OBJECT_MAPPER.writeValueAsString(
                    Map.of("status", statusCode, "error", message)
            );
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] bytes = json.getBytes();
            exchange.sendResponseHeaders(statusCode, bytes.length);
            responseSent = true;
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (IOException ioEx) {
            LOGGER.log(Level.SEVERE, "Failed to send error response", ioEx);
        }
    }
}