package org.example.repos;

import org.example.Dto.SignIn.UserSignInReq;
import org.example.Dto.SignIn.UserSignInRes;
import org.example.Dto.UserSignUpReq;
import org.example.Dto.UserSignUpRes;
import org.example.Dto.user.User;
import org.example.exception.DuplicateResourceException;
import org.example.exception.InvalidPasswordException;
import org.example.exception.UserNotFoundException;
import org.example.utils.JWTUtil;
import org.example.utils.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepo {
    String DB_URL = "jdbc:mysql://localhost:3306/chatApp";
    String DB_USER = "root";
    String DB_PASS = "admin1234";
    public UserSignUpRes createUser (UserSignUpReq userSignUpReq) throws Exception {
        System.out.println("[DEBUG] UserRepo createUser method called for email: " + userSignUpReq.getEmail());
        String insertQuery = "Insert into users (username, email, password) values (?,?,?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[DEBUG] Connecting to database: " + DB_URL + " with user: " + DB_USER);
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                System.out.println("[DEBUG] Connection established successfully!");
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        insertQuery, Statement.RETURN_GENERATED_KEYS
                )) {
                    System.out.println("[DEBUG] PreparedStatement created. Setting values...");
                    preparedStatement.setString(1, userSignUpReq.getUsername());
                    preparedStatement.setString(2, userSignUpReq.getEmail());
                    preparedStatement.setString(3, PasswordHasher.hashedPassword(userSignUpReq.getPassword()));
                    
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
        } catch (SQLIntegrityConstraintViolationException e) {
            // Duplicate email — DB unique constraint violated
            throw new DuplicateResourceException("Email is already registered");
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
        Class.forName("com.mysql.cj.jdbc.Driver");
        try ( Connection connection = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS))
        {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query))
            {
                preparedStatement.setString(1, userSignInReq.getEmail());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String password = resultSet.getString("password");
                        if (PasswordHasher.verifyPassword(userSignInReq.getPassword(), password)) {
                            long userId = resultSet.getLong("id");
                            JWTUtil jwtUtil = new JWTUtil();
                            String token = jwtUtil.generateToken(userId);
                            return new UserSignInRes(userId, "User Signed In Successfully...", token);
                        } else {
                            // Wrong password → HTTP 401
                            throw new InvalidPasswordException("Incorrect password");
                        }
                    } else {
                        // No user row found → HTTP 404
                        throw new UserNotFoundException("User with email " + userSignInReq.getEmail() + " not found");
                    }

                }
            }
        }
    }
    public User getUserById(int id) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)){
            String query = "Select username, email from users where id= ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setInt(1,id);
                try (ResultSet resultSet = preparedStatement.executeQuery()){
                    if(resultSet.next()){
                        String username = resultSet.getString("username");
                        String email = resultSet.getString("email");
                        return new User(
                                id,username,email
                        );
                }
            }
        }
            }
        return null;
    }

    /**
     * Search users who are not yet friends/have no pending request with the given user.
     * Filters by username or email containing the search query (case-insensitive).
     *
     * @param id    the current user's id
     * @param query search term (empty/null returns all non-friend users)
     * @return list of matching User objects
     */
    public List<User> getUsersNotFriendship(int id, String query) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String likeParam = (query == null || query.isBlank()) ? "%" : "%" + query.trim() + "%";

        String sql =
            "SELECT id, username, email FROM users " +
            "WHERE id != ? " +
            "AND id NOT IN (" +
            "  SELECT receiver_id FROM friends WHERE sender_id = ? " +
            "  UNION SELECT sender_id FROM friends WHERE receiver_id = ?" +
            ") " +
            "AND id NOT IN (" +
            "  SELECT receiver_id FROM friend_requests WHERE sender_id = ? " +
            "  UNION SELECT sender_id FROM friend_requests WHERE receiver_id = ?" +
            ") " +
            "AND (username LIKE ? OR email LIKE ?) " +
            "LIMIT 20";

        List<User> results = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, id);
            ps.setInt(3, id);
            ps.setInt(4, id);
            ps.setInt(5, id);
            ps.setString(6, likeParam);
            ps.setString(7, likeParam);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email")
                    ));
                }
            }
        }
        return results;
    }
}