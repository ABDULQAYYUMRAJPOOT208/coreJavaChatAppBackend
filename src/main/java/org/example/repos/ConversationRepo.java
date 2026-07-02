package org.example.repos;

import org.example.Dto.chat.ConversationDTO;
import org.example.Dto.chat.MessageDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationRepo {
    private final String DB_URL = "jdbc:mysql://localhost:3306/chatApp";
    private final String DB_USER = "root";
    private final String DB_PASS = "admin1234";

    public List<ConversationDTO> getConversations(int userId) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        List<ConversationDTO> list = new ArrayList<>();

        String query = 
            "SELECT " +
            "    c.id AS conversation_id, " +
            "    c.type AS conversation_type, " +
            "    c.name AS conversation_name, " +
            "    c.last_message_at, " +
            "    friend.id AS friend_id, " +
            "    friend.username AS friend_username, " +
            "    friend.email AS friend_email, " +
            "    m.content AS last_message_content, " +
            "    m.timestamp AS last_message_timestamp, " +
            "    (SELECT COUNT(*) FROM messages msg " +
            "     WHERE msg.conversation_id = c.id " +
            "       AND msg.timestamp > COALESCE(cp1.joined_at, '1970-01-01 00:00:00') " +
            "       AND msg.sender_id != cp1.user_id " +
            "    ) AS unread_count " +
            "FROM conversation_participants cp1 " +
            "JOIN conversations c ON cp1.conversation_id = c.id " +
            "LEFT JOIN conversation_participants cp2 ON cp2.conversation_id = c.id AND cp2.user_id != cp1.user_id " +
            "LEFT JOIN users friend ON cp2.user_id = friend.id " +
            "LEFT JOIN messages m ON m.conversation_id = c.id " +
            "  AND m.id = ( " +
            "      SELECT id FROM messages " +
            "      WHERE conversation_id = c.id " +
            "      ORDER BY timestamp DESC, id DESC " +
            "      LIMIT 1 " +
            "  ) " +
            "WHERE cp1.user_id = ? " +
            "ORDER BY COALESCE(c.last_message_at, c.created_at) DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int conversationId = rs.getInt("conversation_id");
                    String type = rs.getString("conversation_type");
                    String name = rs.getString("conversation_name");
                    int friendId = rs.getInt("friend_id");
                    String friendUsername = rs.getString("friend_username");
                    String friendEmail = rs.getString("friend_email");
                    String lastMessageContent = rs.getString("last_message_content");
                    Timestamp ts = rs.getTimestamp("last_message_timestamp");
                    String lastMessageTimestamp = (ts != null) ? ts.toString() : null;
                    int unreadCount = rs.getInt("unread_count");

                    list.add(new ConversationDTO(
                        conversationId, type, name, friendId, friendUsername, friendEmail,
                        lastMessageContent, lastMessageTimestamp, unreadCount
                    ));
                }
            }
        }
        return list;
    }

    public List<MessageDTO> getMessagesForConversation(int conversationId) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        List<MessageDTO> list = new ArrayList<>();

        String query = "SELECT id, conversation_id, sender_id, content, timestamp, status, message_type " +
                       "FROM messages WHERE conversation_id = ? ORDER BY timestamp ASC, id ASC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, conversationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int convId = rs.getInt("conversation_id");
                    int senderId = rs.getInt("sender_id");
                    String content = rs.getString("content");
                    Timestamp ts = rs.getTimestamp("timestamp");
                    String timestamp = (ts != null) ? ts.toString() : null;
                    String status = rs.getString("status");
                    String messageType = rs.getString("message_type");

                    list.add(new MessageDTO(id, convId, senderId, content, timestamp, status, messageType));
                }
            }
        }
        return list;
    }

    public boolean sendMessage(int conversationId, int senderId, String content) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String insertQuery = "INSERT INTO messages (conversation_id, sender_id, content) VALUES (?, ?, ?)";
        String updateConvQuery = "UPDATE conversations SET last_message_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psInsert = conn.prepareStatement(insertQuery)) {
                    psInsert.setInt(1, conversationId);
                    psInsert.setInt(2, senderId);
                    psInsert.setString(3, content);
                    psInsert.executeUpdate();
                }

                try (PreparedStatement psUpdate = conn.prepareStatement(updateConvQuery)) {
                    psUpdate.setInt(1, conversationId);
                    psUpdate.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
