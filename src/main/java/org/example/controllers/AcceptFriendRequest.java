package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.example.exception.BaseHandler;
import org.example.repos.RequestRepo;

public class AcceptFriendRequest extends BaseHandler {
    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed. Use POST.");
            return;
        }
        String userIdStr = (String) exchange.getAttribute("User-Id");
        int userId = Integer.parseInt(userIdStr);
        String path = exchange.getRequestURI().getPath();
        int senderId = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
        RequestRepo requestRepo = new RequestRepo();
        boolean result = requestRepo.acceptFriendRequest(senderId, userId);
        if (result) {
            sendJson(exchange, 200, java.util.Map.of("message", "Friend request accepted"));
        } else {
            sendJson(exchange, 400, java.util.Map.of("error", "Friend request not accepted"));
        }
    }
}