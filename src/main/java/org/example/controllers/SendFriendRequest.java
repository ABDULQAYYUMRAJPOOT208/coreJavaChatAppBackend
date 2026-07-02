package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.example.Dto.request.SendRequestReq;
import org.example.exception.BadRequestException;
import org.example.exception.BaseHandler;
import org.example.services.FriendRequestService;

import java.io.InputStream;

public class SendFriendRequest extends BaseHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FriendRequestService friendRequestService = new FriendRequestService();

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        System.out.println("SendFriendRequest handleRequest called");

        if (!"POST".equals(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed. Use POST.");
            return;
        }

        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes());
        System.out.println("Request body: " + requestBody);
        String path = exchange.getRequestURI().getPath();
        String receiverId = path.substring(path.lastIndexOf("/") + 1);

        if (receiverId.isEmpty()) {
            throw new BadRequestException("Receiver ID cannot be empty.");
        }

        String senderId = (String) exchange.getAttribute("User-Id");
        if (senderId == null || senderId.trim().isEmpty()) {
            throw new BadRequestException("Missing sender User-Id.");
        }


        friendRequestService.sendFriendRequest(senderId, receiverId);

        sendText(exchange, 201, "Friend request successfully sent.");
    }
}