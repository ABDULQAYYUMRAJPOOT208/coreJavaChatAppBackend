package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer {
    private static final int PORT = 8081;
    public static final List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Socket server is listening on PORT:" + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(socket);
                synchronized (clients) {
                    clients.add(clientHandler);
                }
                new Thread(clientHandler).start();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void addClient(ClientHandler clientHandler) {
        synchronized (clients) {
            clients.add(clientHandler);
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        synchronized (clients) {
            clients.remove(clientHandler);
        }
    }

    public static List<ClientHandler> getClients() {
        synchronized (clients) {
            return new ArrayList<>(clients);
        }
    }
}
