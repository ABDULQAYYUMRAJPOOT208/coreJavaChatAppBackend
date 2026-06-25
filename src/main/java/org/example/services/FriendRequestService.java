package org.example.services;

import org.example.repos.RequestRepo;

public class FriendRequestService {
    public int sendFriendRequest(String senderId, String recieverId) throws Exception
    {
        if(recieverId == null)
        {
            throw new Exception("Friend ID is required");
        }
        int sId = Integer.parseInt(senderId);
        int rId = Integer.parseInt(recieverId);
        RequestRepo requestRepo = new RequestRepo();

        int id = requestRepo.createFriendRequest(sId,rId);
        if(id < 0)
        {
            throw new Exception("Friend request failed");
        }
        return id;
    }

}
