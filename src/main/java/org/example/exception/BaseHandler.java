package org.example.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class BaseHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(BaseHandler.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private boolean responseSent = false;

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        try {
            handleRequest(exchange);
        } catch (AppException ex) {
            LOGGER.log(Level.WARNING, "AppException [{0}]: {1}",
                    new Object[]{ex.getStatusCode(), ex.getMessage()});
            if (!responseSent) {
                sendErrorResponse(exchange, ex.getStatusCode(), ex.getMessage());
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unhandled exception in " + getClass().getSimpleName(), ex);
            if (!responseSent) {
                sendErrorResponse(exchange, 500, "Internal server error. Please try again later.");
            }
        } finally {
            exchange.close();
        }
    }

    protected abstract void handleRequest(HttpExchange exchange) throws Exception;
    protected void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        if (statusCode == 204) {
            exchange.sendResponseHeaders(statusCode, -1);
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

    protected void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        if (statusCode == 204) {
            exchange.sendResponseHeaders(statusCode, -1);
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


    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) {
        try {
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