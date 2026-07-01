package org.example.repos;

import org.example.Dto.request.FriendRequestDTO;
import org.example.Dto.user.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestRepo {
    private String DB_URL = "jdbc:mysql://localhost:3306/chatApp";
    private  String DB_USER = "root";
    private  String DB_PASS = "admin1234";
    public int createFriendRequest(int senderId, int receiverId) throws Exception
    {
        System.out.println("Create Freien Request is called with " + senderId + "  " + receiverId);


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

    public List<FriendRequestDTO> getAllPendingRequests(int activeUserId) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        List<FriendRequestDTO> requestsList = new ArrayList<>();

        String unifiedQuery =
                "SELECT u.id AS user_id, u.username, u.email, "
                        + "CASE WHEN fr.sender_id = ? THEN 0 ELSE 1 END AS is_incoming "
                        + "FROM friend_requests fr "
                        + "JOIN users u ON (fr.sender_id = ? AND fr.receiver_id = u.id) "
                        + "             OR (fr.receiver_id = ? AND fr.sender_id = u.id) "
                        + "WHERE (fr.sender_id = ? OR fr.receiver_id = ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = connection.prepareStatement(unifiedQuery)) {

            stmt.setInt(1, activeUserId);
            stmt.setInt(2, activeUserId);
            stmt.setInt(3, activeUserId);
            stmt.setInt(4, activeUserId);
            stmt.setInt(5, activeUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    boolean isIncoming = rs.getInt("is_incoming") == 1;

                    requestsList.add(new FriendRequestDTO(userId, username, email, isIncoming));
                }
            }
        }
        return requestsList;
    }

    public int deleteRequest(int id, int receiverId) throws SQLException {
        String deleteRequestQuery = "DELETE FROM friend_requests WHERE sender_id = ? AND receiver_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteRequestQuery)) {

            deleteStmt.setInt(1, id);
            deleteStmt.setInt(2, receiverId);

            int affectedRows = deleteStmt.executeUpdate();
            return affectedRows > 0 ? 1 : -1;
        }
    }
}
