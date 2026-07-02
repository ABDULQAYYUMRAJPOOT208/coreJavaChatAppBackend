package org.example.Dto.request;

public class FriendRequestDTO {
    private int id;
    private String username;
    private String email;
    private boolean incoming;

    public FriendRequestDTO(int id, String username, String email, boolean incoming) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.incoming = incoming;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isIncoming() { return incoming; }
}
