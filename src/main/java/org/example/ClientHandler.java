package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import org.example.Dto.chat.MessageDTO;
import org.example.repos.ConversationRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Integer userId;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                if (message.isBlank()) {
                    continue;
                }

                JsonNode payload = objectMapper.readTree(message);
                if (payload.has("type") && "auth".equals(payload.get("type").asText())) {
                    if (payload.has("userId")) {
                        this.userId = payload.get("userId").asInt();
                    }
                    continue;
                }

                MessageDTO messageDTO = objectMapper.treeToValue(payload, MessageDTO.class);
                ConversationRepo conversationRepo = new ConversationRepo();
                MessageDTO savedMessage = conversationRepo.sendMessageAndReturn(
                        messageDTO.getConversationId(),
                        messageDTO.getSenderId(),
                        messageDTO.getContent()
                );

                if (savedMessage != null) {
                    broadcastToConversationParticipants(savedMessage);
                    System.out.println("Message sent successfully...");
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
    }

    public void sendMessage(MessageDTO messageDTO) {
        try {
            out.println(objectMapper.writeValueAsString(messageDTO));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getUserId() {
        return userId;
    }

    private void broadcastToConversationParticipants(MessageDTO messageDTO) throws Exception {
        ConversationRepo conversationRepo = new ConversationRepo();
        List<Integer> participantIds = conversationRepo.getParticipantUserIds(messageDTO.getConversationId());
        for (Integer participantId : participantIds) {
            if (participantId == null || participantId == messageDTO.getSenderId()) {
                continue;
            }
            for (ClientHandler client : SocketServer.getClients()) {
                if (participantId.equals(client.getUserId())) {
                    client.sendMessage(messageDTO);
                }
            }
        }
    }

    private void cleanup() {
        try {
            SocketServer.removeClient(this);
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
