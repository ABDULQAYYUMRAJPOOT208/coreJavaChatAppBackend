package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.example.exception.BaseHandler;
import org.example.repos.RequestRepo;

public class DeleteRequestController extends BaseHandler {
    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        if(!"DELETE".equals(exchange.getRequestMethod()))
        {
            sendText(exchange, 405, "Method Not Allowed. Use DELETE.");
            return;
        }
        String userIdStr = (String) exchange.getAttribute("User-Id");
        if (userIdStr == null) {
            sendText(exchange, 401, "Unauthorized");
            return;
        }
        int userId  = Integer.parseInt(userIdStr);
        String path = exchange.getRequestURI().getPath();
        int receiverId =Integer.parseInt( path.substring(path.lastIndexOf("/") + 1));
        RequestRepo requestRepo = new RequestRepo();
        int affectedRows = requestRepo.deleteRequest(userId, receiverId);
        if(affectedRows > 0)
        {
            sendJson(exchange,204, "Request deleted successfully");
        }else{
            sendJson(exchange,404, "Request not found");
        }
    }


}
