package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer {
    private  static final  int PORT = 8081;
    public static List<ClientHandler> clients = new ArrayList<>();
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Socket servre is listening on PORT:"+ PORT);

            while(true)
            {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(socket);
                synchronized (clients)
                {
                    clients.add(clientHandler);
                }
                new Thread(clientHandler).start();
            }
        }catch (IOException ioException)
        {
ioException.printStackTrace();
        }
    }

    public static void removeClient(ClientHandler clientHandler)
    {
        clients.remove(clientHandler);
    }
}
