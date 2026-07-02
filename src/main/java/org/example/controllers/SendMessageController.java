package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.example.Dto.chat.SendMessageReq;
import org.example.exception.BadRequestException;
import org.example.exception.BaseHandler;
import org.example.repos.ConversationRepo;

import java.io.InputStream;
import java.util.Map;

public class SendMessageController extends BaseHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed. Use POST.");
            return;
        }

        String userIdStr = (String) exchange.getAttribute("User-Id");
        if (userIdStr == null) {
            sendText(exchange, 401, "Unauthorized");
            return;
        }
        int senderId = Integer.parseInt(userIdStr);

        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes());

        SendMessageReq req = objectMapper.readValue(requestBody, SendMessageReq.class);
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw new BadRequestException("Message content cannot be empty.");
        }

        ConversationRepo repo = new ConversationRepo();
        boolean result = repo.sendMessage(req.getConversationId(), senderId, req.getContent());

        if (result) {
            sendJson(exchange, 201, Map.of("message", "Message sent successfully"));
        } else {
            sendJson(exchange, 400, Map.of("error", "Failed to send message"));
        }
    }
}
