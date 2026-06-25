package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.services.FriendRequestService;

import java.io.IOException;
import java.io.OutputStream;

public class SendFriendRequest implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("SendFriendRequest handle method called");
        try {
            if ("POST".equals(httpExchange.getRequestMethod())) {
                String path = httpExchange.getRequestURI().getPath();
                String baseContext = "/request/send/";
                if (path == null || !path.startsWith(baseContext) || path.length() <= baseContext.length()) {
                    sendResponse(httpExchange, 400, "Error while sending friend request: Invalid receiver ID in path.");
                    return;
                }
                String receiverId = path.substring(baseContext.length()).trim();
                if (receiverId.isEmpty()) {
                    sendResponse(httpExchange, 400, "Error while sending friend request: Receiver ID cannot be empty.");
                    return;
                }

                String senderId = (String) httpExchange.getAttribute("User-Id");
                if (senderId == null || senderId.trim().isEmpty()) {
                    sendResponse(httpExchange, 401, "Error while sending friend request: Unauthorized: Missing sender User-Id.");
                    return;
                }

                FriendRequestService friendRequestService = new FriendRequestService();
                int id = friendRequestService.sendFriendRequest(senderId, receiverId);
                String response = "Friend request successfully sent...";
                sendResponse(httpExchange, 201, response);
            } else {
                sendResponse(httpExchange, 405, "Method Not Allowed. Use POST.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Added for debugging
            String response = "Error while sending friend request: " + e.getMessage();
            sendResponse(httpExchange, 400, response);
        }
    }

    private void sendResponse(HttpExchange httpExchange, int statusCode, String response) throws IOException {
        httpExchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}