package org.example.repos;

import org.example.Dto.SignIn.UserSignInReq;
import org.example.Dto.SignIn.UserSignInRes;
import org.example.Dto.UserSignUpReq;
import org.example.Dto.UserSignUpRes;
import org.example.utils.JWTUtil;

import java.sql.*;

public class UserRepo {
    String DB_URL = "jdbc:mysql://localhost:3306/chatApp";
    String DB_USER = "root";
    String DB_PASS = "admin1234";
    public UserSignUpRes createUser (UserSignUpReq userSignUpReq) throws Exception {
        System.out.println("[DEBUG] UserRepo createUser method called for email: " + userSignUpReq.getEmail());
        String insertQuery = "Insert into users (username, email, password) values (?,?,?)";

        try {
            System.out.println("[DEBUG] Connecting to database: " + DB_URL + " with user: " + DB_USER);
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                System.out.println("[DEBUG] Connection established successfully!");
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        insertQuery, Statement.RETURN_GENERATED_KEYS
                )) {
                    System.out.println("[DEBUG] PreparedStatement created. Setting values...");
                    preparedStatement.setString(1, userSignUpReq.getUsername());
                    preparedStatement.setString(2, userSignUpReq.getEmail());
                    preparedStatement.setString(3, userSignUpReq.getPassword());
                    
                    System.out.println("[DEBUG] Executing update...");
                    int affectedRows = preparedStatement.executeUpdate();
                    System.out.println("[DEBUG] Update executed. Affected rows: " + affectedRows);

                    if (affectedRows > 0) {
                        System.out.println("[DEBUG] Retrieving generated keys...");
                        try (ResultSet generatedKey = preparedStatement.getGeneratedKeys()) {
                            if (generatedKey.next()) {
                                long userId = generatedKey.getLong(1);
                                System.out.println("[DEBUG] User ID generated: " + userId);
                                
                                System.out.println("[DEBUG] Creating JWTUtil instance...");
                                JWTUtil jwtUtil = new JWTUtil();
                                System.out.println("[DEBUG] JWTUtil instance created. Generating token...");
                                String token = jwtUtil.generateToken(userId);
                                System.out.println("[DEBUG] Token generated successfully: " + token);
                                return new UserSignUpRes(userId, "User Created Successfully...", token);
                            } else {
                                System.out.println("[DEBUG] No generated keys found.");
                            }
                        } catch (Throwable t) {
                            System.err.println("[DEBUG] Error inside generated key reading/JWT generation: " + t.getMessage());
                            t.printStackTrace();
                            throw new Exception("Error during key processing or token generation", t);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            System.err.println("[DEBUG] Catch block caught Throwable in UserRepo createUser: " + e.getMessage());
            e.printStackTrace();
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception("Database/Internal error during user creation: " + e.getMessage(), e);
            }
        }

        throw new RuntimeException("Creating user failed, no rows affected.");
    }
    public UserSignInRes signInUser(UserSignInReq userSignInReq) throws Exception {
        String query = "Select * from users where email = ?";
        try ( Connection connection = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS))
        {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query))
            {
                preparedStatement.setString(1, userSignInReq.getEmail());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String password = resultSet.getString("password");
                        if (password.equals(userSignInReq.getPassword())) {
                            long userId = resultSet.getLong("id");
                            JWTUtil jwtUtil = new JWTUtil();
                            String token = jwtUtil.generateToken(userId);
                            return new UserSignInRes(userId, "User Signed In Successfully...", token);
                        } else {
                            throw new Exception("Invalid password");

                        }
                    } else {
                        throw new Exception("User not found");
                    }

                }
            }
        }
    }
}