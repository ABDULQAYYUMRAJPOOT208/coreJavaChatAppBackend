package org.example.controllers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.example.Dto.chat.CreateGroupReq;
import org.example.exception.BadRequestException;
import org.example.exception.BaseHandler;
import org.example.repos.ConversationRepo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

public class CreateGroupController extends BaseHandler {
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
        int userId = Integer.parseInt(userIdStr);

        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes());

        CreateGroupReq req = objectMapper.readValue(requestBody, CreateGroupReq.class);
        if (req.getGroupName() == null || req.getGroupName().isBlank()) {
            throw new BadRequestException("Group name cannot be empty.");
        }
        if (req.getParticipantEmails() == null || req.getParticipantEmails().isEmpty()) {
            throw new BadRequestException("At least one participant email is required.");
        }

        ConversationRepo conversationRepo = new ConversationRepo();
        List<Integer> participantIds = new ArrayList<>();
        for (String email : req.getParticipantEmails()) {
            Integer participantId = conversationRepo.getUserIdByEmail(email.trim());
            if (participantId == null) {
                throw new BadRequestException("Participant not found: " + email);
            }
            if (participantId == userId) {
                continue;
            }
            participantIds.add(participantId);
        }

        if (participantIds.isEmpty()) {
            throw new BadRequestException("Please add at least one valid participant besides yourself.");
        }

        int conversationId = conversationRepo.createGroupConversation(userId, req.getGroupName(), participantIds);
        sendJson(exchange, 201, Map.of("conversationId", conversationId, "message", "Group created successfully."));
    }
}
