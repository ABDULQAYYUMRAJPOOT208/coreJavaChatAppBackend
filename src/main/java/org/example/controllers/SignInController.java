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
    public void handle(HttpExchange exchange)
    {
        if("POST".equals(exchange.getRequestMethod()))
        {
            try{

            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            UserSignInReq userSignInReq = objectMapper.readValue(requestBody, UserSignInReq.class);
            System.out.println("Sigin in request is >> "+ userSignInReq);
                AuthService authService = new AuthService();
                UserSignInRes userSignInRes = authService.signInUser(userSignInReq);

            String response =userSignInRes.toString();
            exchange.sendResponseHeaders(200, response.length());
            try(OutputStream os = exchange.getResponseBody())
            {
                os.write(response.getBytes());
            }
            }
            catch (Exception e)
            {
                System.err.println("Error in Sign in handler >> "+ e);
            }
        }
    }
}
