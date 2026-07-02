package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Dto.chat.MessageDTO;
import org.example.repos.ConversationRepo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket)
    {
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println("Message received: " + message);
                    ObjectMapper objectMapper = new ObjectMapper();
                    MessageDTO messageDTO = objectMapper.readValue(message, MessageDTO.class);
                    ConversationRepo conversationRepo = new ConversationRepo();
                    boolean isSent = conversationRepo.sendMessage(messageDTO.getConversationId(),messageDTO.getSenderId(),messageDTO.getContent());
                    if(isSent) System.out.println("Message sent successfully...");
                }
            }catch (IOException ioException)
            {
                ioException.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    private void cleanup()
    {
        try{
            SocketServer.removeClient(this);
            if(in != null) in.close();
            if(out != null) out.close();
            if(socket != null) socket.close();
        }catch (IOException ioException)
        {
            ioException.printStackTrace();

        }
    }
}
