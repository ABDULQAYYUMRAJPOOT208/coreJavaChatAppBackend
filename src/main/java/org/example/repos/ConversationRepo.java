package org.example.repos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.example.Dto.chat.ConversationDTO;
import org.example.Dto.chat.MessageDTO;

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
        return sendMessageAndReturn(conversationId, senderId, content) != null;
    }

    public MessageDTO sendMessageAndReturn(int conversationId, int senderId, String content) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String insertQuery = "INSERT INTO messages (conversation_id, sender_id, content) VALUES (?, ?, ?)";
        String updateConvQuery = "UPDATE conversations SET last_message_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);
            try {
                int generatedId;
                try (PreparedStatement psInsert = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setInt(1, conversationId);
                    psInsert.setInt(2, senderId);
                    psInsert.setString(3, content);
                    psInsert.executeUpdate();

                    try (ResultSet generatedKeys = psInsert.getGeneratedKeys()) {
                        if (!generatedKeys.next()) {
                            throw new SQLException("Creating message failed, no ID obtained.");
                        }
                        generatedId = generatedKeys.getInt(1);
                    }
                }

                try (PreparedStatement psUpdate = conn.prepareStatement(updateConvQuery)) {
                    psUpdate.setInt(1, conversationId);
                    psUpdate.executeUpdate();
                }

                conn.commit();
                return new MessageDTO(generatedId, conversationId, senderId, content, new Timestamp(System.currentTimeMillis()).toString(), "sent", "text");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Integer> getParticipantUserIds(int conversationId) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        List<Integer> userIds = new ArrayList<>();
        String query = "SELECT user_id FROM conversation_participants WHERE conversation_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, conversationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        }
        return userIds;
    }

    public Integer getUserIdByEmail(String email) throws Exception {
        if (email == null || email.isBlank()) {
            return null;
        }
        Class.forName("com.mysql.cj.jdbc.Driver");
        String query = "SELECT id FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }

    public int createGroupConversation(int createdByUserId, String groupName, List<Integer> participantIds) throws Exception {
        if (groupName == null || groupName.isBlank()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }
        if (participantIds == null || participantIds.isEmpty()) {
            throw new IllegalArgumentException("At least one participant is required.");
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        String insertConvQuery = "INSERT INTO conversations (type, name, created_by_user_id) VALUES ('group', ?, ?)";
        String insertParticipantQuery = "INSERT INTO conversation_participants (conversation_id, user_id) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);
            try {
                int generatedConversationId;
                try (PreparedStatement psInsert = conn.prepareStatement(insertConvQuery, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setString(1, groupName.trim());
                    psInsert.setInt(2, createdByUserId);
                    psInsert.executeUpdate();
                    try (ResultSet generatedKeys = psInsert.getGeneratedKeys()) {
                        if (!generatedKeys.next()) {
                            throw new SQLException("Creating group conversation failed, no ID obtained.");
                        }
                        generatedConversationId = generatedKeys.getInt(1);
                    }
                }

                try (PreparedStatement psPart = conn.prepareStatement(insertParticipantQuery)) {
                    Set<Integer> uniqueParticipantIds = new LinkedHashSet<>(participantIds);
                    uniqueParticipantIds.add(createdByUserId);
                    for (Integer participantId : uniqueParticipantIds) {
                        psPart.setInt(1, generatedConversationId);
                        psPart.setInt(2, participantId);
                        psPart.addBatch();
                    }
                    psPart.executeBatch();
                }

                conn.commit();
                return generatedConversationId;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
