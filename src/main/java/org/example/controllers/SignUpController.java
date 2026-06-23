package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SignUpController implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        if("POST".equals(exchange.getRequestMethod()))
        {
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes());
            System.out.println("Request Body is : " + requestBody);
            String response = "User is Created";
            exchange.sendResponseHeaders(201, response.length());

            try(OutputStream os = exchange.getResponseBody()){
                os.write(response.getBytes());
            }

        }
    }
}
