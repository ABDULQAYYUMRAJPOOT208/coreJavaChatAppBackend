package org.example.filters;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import org.example.utils.JWTUtil;

import java.io.IOException;
import java.io.OutputStream;

public class AuthFilter extends Filter {
    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        try {
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
                token = authHeader.substring(7).trim();
                if (token.isEmpty()) {
                    token = null;
                }
            }
            if (token == null) {
                String response = "Unauthorized";
                exchange.sendResponseHeaders(401, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                JWTUtil jwtUtil = new JWTUtil();
                DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
                if (decodedJWT != null) {
                    String userId = decodedJWT.getSubject();
                    exchange.setAttribute("User-Id", userId);
                    chain.doFilter(exchange);
                } else {
                    String response = "Unauthorized";
                    exchange.sendResponseHeaders(401, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in AuthFilter: " + e.getMessage());
            e.printStackTrace();
            String response = "Internal Server Error";
            exchange.sendResponseHeaders(500, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    @Override
    public String description() {
        return "Authentication Filter";
    }
}

