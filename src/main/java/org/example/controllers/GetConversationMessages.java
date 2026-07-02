package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.example.Dto.chat.MessageDTO;
import org.example.exception.BadRequestException;
import org.example.exception.BaseHandler;
import org.example.repos.ConversationRepo;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetConversationMessages extends BaseHandler {
    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed. Use GET.");
            return;
        }

        String rawQuery = exchange.getRequestURI().getQuery(); // e.g. "conversationId=1"
        String convIdStr = null;
        if (rawQuery != null) {
            for (String param : rawQuery.split("&")) {
                if (param.startsWith("conversationId=")) {
                    convIdStr = URLDecoder.decode(
                        param.substring("conversationId=".length()), StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        if (convIdStr == null || convIdStr.isBlank()) {
            throw new BadRequestException("Missing conversationId parameter.");
        }

        int conversationId = Integer.parseInt(convIdStr);
        ConversationRepo conversationRepo = new ConversationRepo();
        List<MessageDTO> messages = conversationRepo.getMessagesForConversation(conversationId);

        sendJson(exchange, 200, messages);
    }
}
