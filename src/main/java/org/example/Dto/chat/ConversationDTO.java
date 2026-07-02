package org.example.Dto.chat;

public class ConversationDTO {
    private int conversationId;
    private String conversationType; // 'private' or 'group'
    private String conversationName; // Null for private, name of group for group chats
    private int friendId;            // User ID of the friend (0 if group)
    private String friendUsername;   // Username of the friend (null if group)
    private String friendEmail;      // Email of the friend (null if group)
    private String lastMessageContent;
    private String lastMessageTimestamp;
    private int unreadCount;

    public ConversationDTO() {}

    public ConversationDTO(int conversationId, String conversationType, String conversationName,
                           int friendId, String friendUsername, String friendEmail,
                           String lastMessageContent, String lastMessageTimestamp, int unreadCount) {
        this.conversationId = conversationId;
        this.conversationType = conversationType;
        this.conversationName = conversationName;
        this.friendId = friendId;
        this.friendUsername = friendUsername;
        this.friendEmail = friendEmail;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.unreadCount = unreadCount;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationType() {
        return conversationType;
    }

    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public String getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(String lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
