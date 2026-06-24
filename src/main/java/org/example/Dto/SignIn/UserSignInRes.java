package org.example.Dto.SignIn;

public class UserSignInRes {
    private long id;
    private String message;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserSignInRes(long id, String message) {
        this.id = id;
        this.message = message;
    }

    public UserSignInRes(long id, String message, String token) {
        this.id = id;
        this.message = message;
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ",token='" + token + '\'' +
                '}';
    }
}
