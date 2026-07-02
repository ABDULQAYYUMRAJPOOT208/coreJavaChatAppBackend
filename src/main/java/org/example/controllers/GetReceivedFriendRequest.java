package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.example.exception.BaseHandler;
import org.example.repos.RequestRepo;

public class GetReceivedFriendRequest extends BaseHandler {
    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        if(!"GET".equals(exchange.getRequestMethod()))
        {
            sendText(exchange, 405, "Method Not Allowed. Use GET.");
            return;
        }
        String userIdStr = (String) exchange.getAttribute("User-Id");
        int userId = Integer.parseInt(userIdStr);
        RequestRepo requestRepo = new RequestRepo();
        sendJson(exchange,200, requestRepo.getAllReceivedRequests(userId));
    }
}
