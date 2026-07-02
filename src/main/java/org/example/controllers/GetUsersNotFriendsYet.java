package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import org.example.Dto.user.User;
import org.example.exception.BaseHandler;
import org.example.repos.UserRepo;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetUsersNotFriendsYet extends BaseHandler {

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        String userId = (String) exchange.getAttribute("User-Id");

        String searchQuery = null;
        String rawQuery = exchange.getRequestURI().getQuery();
        if (rawQuery != null) {
            for (String param : rawQuery.split("&")) {
                if (param.startsWith("query=")) {
                    searchQuery = URLDecoder.decode(
                        param.substring("query=".length()), StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        UserRepo userRepo = new UserRepo();
        List<User> users = userRepo.getUsersNotFriendship(Integer.parseInt(userId), searchQuery);

        sendJson(exchange, 200, users);
    }
}
