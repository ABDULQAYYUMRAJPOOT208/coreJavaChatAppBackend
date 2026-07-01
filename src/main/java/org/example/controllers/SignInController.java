package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Dto.SignIn.UserSignInReq;
import org.example.Dto.SignIn.UserSignInRes;
import org.example.services.AuthService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SignInController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream inputStream = exchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes());
                ObjectMapper objectMapper = new ObjectMapper();
                UserSignInReq userSignInReq = objectMapper.readValue(requestBody, UserSignInReq.class);
                System.out.println("Sign in request is >> " + userSignInReq.toString());
                AuthService authService = new AuthService();
                UserSignInRes userSignInRes = authService.signInUser(userSignInReq);

                String response = objectMapper.writeValueAsString(userSignInRes);
                exchange.getResponseHeaders().set("Content-Type", "application/json");

                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "Method Not Allowed. Use POST.");
            }
        } catch (Exception e) {
            System.err.println("Error in Sign in handler >> " + e);
            e.printStackTrace();
            String response = "Error while signing in: " + e.getMessage();
            sendResponse(exchange, 400, response);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}

