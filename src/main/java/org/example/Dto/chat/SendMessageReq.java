package org.example.Dto.chat;

public class SendMessageReq {
    private int conversationId;
    private String content;

    public SendMessageReq() {}

    public SendMessageReq(int conversationId, String content) {
        this.conversationId = conversationId;
        this.content = content;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
