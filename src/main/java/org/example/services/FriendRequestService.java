package org.example.services;

import org.example.exception.BadRequestException;
import org.example.repos.RequestRepo;

public class FriendRequestService {
    public int sendFriendRequest(String senderId, String recieverId) throws Exception
    {
        if (recieverId == null || recieverId.isBlank()) {
            throw new BadRequestException("Friend ID is required");
        }
        int sId = Integer.parseInt(senderId);
        int rId = Integer.parseInt(recieverId);
        RequestRepo requestRepo = new RequestRepo();

        int id = requestRepo.createFriendRequest(sId,rId);
        if (id < 0) {
            throw new BadRequestException("Could not send friend request");
        }
        return id;
    }

}
