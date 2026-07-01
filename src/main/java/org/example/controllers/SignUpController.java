package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.example.Dto.UserSignUpReq;
import org.example.Dto.UserSignUpRes;
import org.example.exception.BaseHandler;
import org.example.services.AuthService;

import java.io.InputStream;

public class SignUpController extends BaseHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthService authService = new AuthService();

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        System.out.println("SignUpController handleRequest called: " + exchange.getRequestMethod());

        if (!"POST".equals(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed. Use POST.");
            return;
        }

        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes());
        UserSignUpReq userSignUpReq = objectMapper.readValue(requestBody, UserSignUpReq.class);

        // authService.SignUpUser() can freely throw:
        //   DuplicateResourceException → 409 sent automatically
        //   anything else              → 500 sent automatically
        UserSignUpRes userSignUpRes = authService.SignUpUser(userSignUpReq);
        System.out.println("User signed up: " + userSignUpRes);

        sendJson(exchange, 201, userSignUpRes);
    }
}