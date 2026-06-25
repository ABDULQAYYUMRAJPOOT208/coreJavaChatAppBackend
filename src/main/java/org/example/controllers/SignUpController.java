package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Dto.UserSignUpReq;
import org.example.Dto.UserSignUpRes;
import org.example.services.AuthService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SignUpController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("SignUpController handle method called with method: " + exchange.getRequestMethod());
        try {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream inputStream = exchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes());
                ObjectMapper objectMapper = new ObjectMapper();
                UserSignUpReq userSignUpReq = objectMapper.readValue(requestBody, UserSignUpReq.class);
                
                AuthService authService = new AuthService();
                UserSignUpRes userSignUpRes = authService.SignUpUser(userSignUpReq);
                System.out.println("User signed up successfully. Response: " + userSignUpRes.toString());
                String response = userSignUpRes.toString();
                sendResponse(exchange, 201, response);
                System.out.println("Response sent successfully.");
            } else {
                System.out.println("Received non-POST request: " + exchange.getRequestMethod());
                sendResponse(exchange, 405, "Method Not Allowed. Use POST.");
            }
        } catch (Throwable e) {
            System.err.println("Error during user sign up: " + e.getMessage());
            e.printStackTrace();
            String response = "Error while creating user: " + e.getMessage();
            sendResponse(exchange, 500, response);
            System.err.println("Error response sent.");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}