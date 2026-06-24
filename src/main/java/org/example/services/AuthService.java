package org.example.services;

import org.example.Dto.SignIn.UserSignInReq;
import org.example.Dto.SignIn.UserSignInRes;
import org.example.Dto.UserSignUpReq;
import org.example.Dto.UserSignUpRes;
import org.example.repos.UserRepo;

public class AuthService {
    UserRepo userRepo;
    public AuthService()
    {
        userRepo = new UserRepo();
    }
    public UserSignUpRes SignUpUser(UserSignUpReq userSignUpReq) throws Exception {
        System.out.println("AuthService SignUpUser method called for email: " + userSignUpReq.getEmail());
        if(userSignUpReq.getEmail() == null)
        {
            throw new Exception("User Email is required");
        }
        else if (userSignUpReq.getUsername() == null)
        {
            throw new Exception("User Username is required");
        }
        else if (userSignUpReq.getPassword() == null)
        {
            throw new Exception("User Password is required");
        }
        return userRepo.createUser(userSignUpReq);
    }
    public UserSignInRes signInUser(UserSignInReq userSignInReq) throws Exception {
        if(userSignInReq.getEmail() == null)
        {
            throw new Exception("Email is required");
        }else if(userSignInReq.getPassword() == null)
        {
            throw new Exception("Password is required...");
        }
        return userRepo.signInUser(userSignInReq);
    }
}