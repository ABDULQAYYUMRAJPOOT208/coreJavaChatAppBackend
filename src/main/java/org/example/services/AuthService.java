package org.example.services;

import org.example.Dto.SignIn.UserSignInReq;
import org.example.Dto.SignIn.UserSignInRes;
import org.example.Dto.UserSignUpReq;
import org.example.Dto.UserSignUpRes;
import org.example.exception.BadRequestException;
import org.example.repos.UserRepo;

public class AuthService {

    UserRepo userRepo;

    public AuthService() {
        userRepo = new UserRepo();
    }

    public UserSignUpRes SignUpUser(UserSignUpReq userSignUpReq) throws Exception {
        System.out.println("AuthService SignUpUser called for email: " + userSignUpReq.getEmail());

        if (userSignUpReq.getEmail() == null || userSignUpReq.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }
        if (userSignUpReq.getUsername() == null || userSignUpReq.getUsername().isBlank()) {
            throw new BadRequestException("Username is required");
        }
        if (userSignUpReq.getPassword() == null || userSignUpReq.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        // UserRepo can throw DuplicateResourceException, which BaseHandler will catch automatically
        return userRepo.createUser(userSignUpReq);
    }

    public UserSignInRes signInUser(UserSignInReq userSignInReq) throws Exception {
        if (userSignInReq.getEmail() == null || userSignInReq.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }
        if (userSignInReq.getPassword() == null || userSignInReq.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        // UserRepo can throw UserNotFoundException or InvalidPasswordException,
        // which BaseHandler will catch automatically
        return userRepo.signInUser(userSignInReq);
    }
}