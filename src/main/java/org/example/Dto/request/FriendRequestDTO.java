package org.example.Dto.request;

public class FriendRequestDTO {
    private int id;
    private String username;
    private String email;
    private boolean incoming; // true = received (incoming), false = sent

    public FriendRequestDTO(int id, String username, String email, boolean incoming) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.incoming = incoming;
    }

    // Include standard getters and setters for Jackson mapping consistency
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isIncoming() { return incoming; }
}
