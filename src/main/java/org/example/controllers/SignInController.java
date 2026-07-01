package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.example.Dto.SignIn.UserSignInReq;
import org.example.Dto.SignIn.UserSignInRes;
import org.example.exception.BaseHandler;
import org.example.services.AuthService;

import java.io.InputStream;

public class SignInController extends BaseHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthService authService = new AuthService();

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed. Use POST.");
            return;
        }

        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes());
        UserSignInReq userSignInReq = objectMapper.readValue(requestBody, UserSignInReq.class);
        System.out.println("Sign in request >> " + userSignInReq);

        // authService.signInUser() can freely throw:
        //   InvalidPasswordException  → 401 sent automatically
        //   UserNotFoundException     → 404 sent automatically
        //   anything else             → 500 sent automatically
        UserSignInRes userSignInRes = authService.signInUser(userSignInReq);

        sendJson(exchange, 200, userSignInRes);
    }
}
