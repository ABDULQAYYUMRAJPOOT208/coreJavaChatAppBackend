package org.example.controllers;

import org.example.Dto.request.FriendRequestDTO;
import org.example.exception.BaseHandler;
import org.example.repos.RequestRepo;

import java.util.List;

public class GetAllRequestController extends BaseHandler {
    @Override
    protected void handleRequest(com.sun.net.httpserver.HttpExchange exchange) throws Exception {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed. Use GET.");
            return;
        }

        String userId = (String) exchange.getAttribute("User-Id");
        if (userId == null) {
            sendText(exchange, 401, "Unauthorized");
            return;
        }

        RequestRepo requestRepo = new org.example.repos.RequestRepo();
        List<FriendRequestDTO> requests = requestRepo.getAllPendingRequests(Integer.parseInt(userId));

        sendJson(exchange, 200, requests);
    }

}
