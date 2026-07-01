package org.example.Dto.user;

public class User {
    private int id;
    private String username;
    private String email;

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    private String requestStatus;
    public User(int id, String username, String email, String requestStatus)
    {
        this.id = id;
        this
                .username = username;
        this.email = email;
        this.requestStatus = requestStatus;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", requestStatus='" + requestStatus + '\'' +
                '}';
    }

    public User(int id, String username, String email)
    {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
