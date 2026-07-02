package org.example;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.example.controllers.AcceptFriendRequest;
import org.example.controllers.CreateGroupController;
import org.example.controllers.DeleteRequestController;
import org.example.controllers.GetAllRequestController;
import org.example.controllers.GetConversationMessages;
import org.example.controllers.GetConversations;
import org.example.controllers.GetReceivedFriendRequest;
import org.example.controllers.GetUsersNotFriendsYet;
import org.example.controllers.SendFriendRequest;
import org.example.controllers.SendMessageController;
import org.example.controllers.SignInController;
import org.example.controllers.SignUpController;
import org.example.filters.AuthFilter;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {
    public static void main(String[] args) {
        HttpServer server = null;
        System.out.println("Starting Server...");
        try{

            server = HttpServer.create( new InetSocketAddress("localhost",8080), 0);
            server.createContext("/", new RootHandler());
            server.createContext("/auth/sign-up", new SignUpController());
            server.createContext("/auth/sign-in", new SignInController());

            HttpContext getUsersNotFriendsYet = server.createContext("/users/get-not-friends-yet", new GetUsersNotFriendsYet());
            getUsersNotFriendsYet.getFilters().add(new AuthFilter());

            HttpContext requestContext =  server.createContext("/request/send/", new SendFriendRequest());
            requestContext.getFilters().add(new AuthFilter());

            HttpContext getAllRequest = server.createContext("/request/all", new GetAllRequestController());
            getAllRequest.getFilters().add(new AuthFilter());

            HttpContext cancelRequest = server.createContext("/request/cancel/", new DeleteRequestController());
            cancelRequest.getFilters().add(new AuthFilter());

            HttpContext getReceivedRequests = server.createContext("/request/received", new GetReceivedFriendRequest());
            getReceivedRequests.getFilters().add(new AuthFilter());

            HttpContext acceptRequest = server.createContext("/request/accept/", new AcceptFriendRequest());
            acceptRequest.getFilters().add(new AuthFilter());

            HttpContext getConversations = server.createContext("/conversations/all", new GetConversations());
            getConversations.getFilters().add(new AuthFilter());

            HttpContext getConversationMessages = server.createContext("/conversations/messages", new GetConversationMessages());
            getConversationMessages.getFilters().add(new AuthFilter());

            HttpContext createGroup = server.createContext("/conversations/create-group", new CreateGroupController());
            createGroup.getFilters().add(new AuthFilter());

            HttpContext sendMessage = server.createContext("/messages/send", new SendMessageController());
            sendMessage.getFilters().add(new AuthFilter());

            Thread socketThread = new Thread(SocketServer::startServer);
            socketThread.setDaemon(true);
            socketThread.start();

            server.start();
            System.out.println("Server is running on http://localhost:" + 8080);
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
        String path = exchange.getRequestURI().getPath();

        if ("/".equals(path)) {
            String response = "Server is Running...";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            String response = "404 Not Found: Route does not exist";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
