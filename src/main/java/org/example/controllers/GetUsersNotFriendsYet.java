package org.example.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.repos.UserRepo;

import java.io.IOException;
import java.io.InputStream;

public class GetUsersNotFriendsYet implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("GetUsersNotFriendsYet handle method called");
        if("GET".equals(exchange.getRequestMethod()))
        {
            System.out.println("GET request received");
            String userId =(String) exchange.getAttribute("User-Id");
            if(userId == null)
            {

            }else{
                System.out.println("User Id: " + userId);
                UserRepo userRepo = new UserRepo();
                userRepo.getUsersNotFriendsyet(Integer.parseInt(userId));
            }
        }
    }
}
