package org.example.Dto.chat;

import java.util.List;

public class CreateGroupReq {
    private String groupName;
    private List<String> participantEmails;

    public CreateGroupReq() {}

    public CreateGroupReq(String groupName, List<String> participantEmails) {
        this.groupName = groupName;
        this.participantEmails = participantEmails;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getParticipantEmails() {
        return participantEmails;
    }

    public void setParticipantEmails(List<String> participantEmails) {
        this.participantEmails = participantEmails;
    }
}
