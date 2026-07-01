package org.example;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.controllers.GetUsersNotFriendsYet;
import org.example.controllers.SendFriendRequest;
import org.example.controllers.SignInController;
import org.example.controllers.SignUpController;
import org.example.filters.AuthFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
public class Main {
    public static void main(String[] args) {
        HttpServer server = null;
        try{
            server= HttpServer.create( new InetSocketAddress("localhost",8080), 0);
            server.createContext("/", new RootHandler());
            server.createContext("/auth/sign-up", new SignUpController());
            server.createContext("/auth/sign-in", new SignInController());

            HttpContext getUsersNotFriendsYet = server.createContext("/users/get-not-friends-yet", new GetUsersNotFriendsYet());
            getUsersNotFriendsYet.getFilters().add(new AuthFilter());

            HttpContext requestContext =  server.createContext("/request/send/", new SendFriendRequest());
            requestContext.getFilters().add(new AuthFilter());

            server.start();
            System.out.println("Server is running on http://localhost:" + 8080);
//            Thread.currentThread().join();
        }catch (Exception e)
        {
            System.out.println("Exception Occurred >> " + e);
            e.printStackTrace();
        }

    }
}

class RootHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "Server is Running...";
        exchange.sendResponseHeaders(200, response.length());
        try(OutputStream os = exchange.getResponseBody()){
            os.write(response.getBytes());
        }
    }
}
