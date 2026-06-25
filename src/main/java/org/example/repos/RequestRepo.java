package org.example.repos;

import java.sql.*;

public class RequestRepo {
    public int createFriendRequest(int senderId, int receiverId) throws Exception
    {
        System.out.println("Create Freien Request is called with " + senderId + "  " + receiverId);
        String DB_URL = "jdbc:mysql://localhost:3306/chatApp";
        String DB_USER = "root";
        String DB_PASS = "admin1234";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS)){
            String checkIfAlreadyExists = "Select * from friend_requests where sender_id = ? and receiver_id = ?";
            String createRequest = "Insert into friend_requests (sender_id, receiver_id) values (?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(checkIfAlreadyExists)) {

                preparedStatement.setInt(1, senderId);
                preparedStatement.setInt(2, receiverId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {

                            throw new Exception("Friend request already sent");
                    }
                }
            }
            try(PreparedStatement insertStmt = connection.prepareStatement(createRequest, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, senderId);
                insertStmt.setInt(2, receiverId);
                int affectedRows = insertStmt.executeUpdate();

                if(affectedRows > 0)
                {
                   try(ResultSet resultSet = insertStmt.getGeneratedKeys())
                   {
                       if(resultSet.next())
                       {
                           return resultSet.getInt(1);
                       }
                   }
                }

            }
        }

        return -1;
    }
}
