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
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("SignUpController handle method called with method: " + exchange.getRequestMethod());
        if("POST".equals(exchange.getRequestMethod()))
        {
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            UserSignUpReq userSignUpReq = objectMapper.readValue(requestBody, UserSignUpReq.class);
            try{
                AuthService authService = new AuthService();
                UserSignUpRes userSignUpRes = authService.SignUpUser(userSignUpReq);
                System.out.println("User signed up successfully. Response: " + userSignUpRes.toString());
                String response = userSignUpRes.toString();
                exchange.sendResponseHeaders(201, response.length());

                try(OutputStream os = exchange.getResponseBody()){
                    os.write(response.getBytes());
                }
                System.out.println("Response sent successfully.");
            }catch (Throwable e)
            {
                System.err.println("Error during user sign up: " + e.getMessage());
                e.printStackTrace();
                String response = "Error while creating user: " + e.getMessage();
                exchange.sendResponseHeaders(500, response.length());
                try(OutputStream os = exchange.getResponseBody()){
                    os.write(response.getBytes());
                }
                System.err.println("Error response sent.");
            }
        } else {
            System.out.println("Received non-POST request: " + exchange.getRequestMethod());
            String response = "Method Not Allowed";
            exchange.sendResponseHeaders(405, response.length());
            try(OutputStream os = exchange.getResponseBody()){
                os.write(response.getBytes());
            }
        }
    }
}