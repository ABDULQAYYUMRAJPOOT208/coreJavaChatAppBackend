package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.example.Dto.chat.ConversationDTO;
import org.example.exception.BaseHandler;
import org.example.repos.ConversationRepo;

import java.util.List;

public class GetConversations extends BaseHandler {
    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed. Use GET.");
            return;
        }

        String userIdStr = (String) exchange.getAttribute("User-Id");
        if (userIdStr == null) {
            sendText(exchange, 401, "Unauthorized");
            return;
        }

        int userId = Integer.parseInt(userIdStr);
        ConversationRepo conversationRepo = new ConversationRepo();
        List<ConversationDTO> conversations = conversationRepo.getConversations(userId);

        sendJson(exchange, 200, conversations);
    }
}
