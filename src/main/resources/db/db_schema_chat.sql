--
-- Chat Application Schema Extensions
--
-- This script adds tables required for chat functionality:
--   - conversations: Represents a chat thread (private or group)
--   - conversation_participants: Links users to conversations and tracks read status
--   - messages: Stores individual chat messages
--

-- 1. Create the 'conversations' table
CREATE TABLE IF NOT EXISTS conversations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- 'private' for 1-on-1, 'group' for group chats
    name VARCHAR(255) NULL,    -- Name for group chats, NULL for private chats
    created_by_user_id INT UNSIGNED NULL, -- Changed to nullable for SET NULL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_message_at TIMESTAMP NULL, -- Timestamp of the last message in this conversation

    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 2. Create the 'conversation_participants' table
--    This table links users to conversations and tracks read status.
CREATE TABLE IF NOT EXISTS conversation_participants (
    conversation_id INT NOT NULL,
    user_id INT UNSIGNED NOT NULL, -- Corrected to INT UNSIGNED
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_read_message_id INT NULL, -- ID of the last message this user has read in this conversation
    is_admin BOOLEAN DEFAULT FALSE, -- For group chats: true if the user is an admin of the group
    left_at TIMESTAMP NULL,         -- Timestamp if the user left the conversation/group

    PRIMARY KEY (conversation_id, user_id),
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    -- FOREIGN KEY (last_read_message_id) REFERENCES messages(id) ON DELETE SET NULL -- Will add after messages table
);

-- 3. Create the 'messages' table
CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT NOT NULL,
    sender_id INT UNSIGNED NULL, -- Changed to nullable for SET NULL
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'sent', -- e.g., 'sent', 'delivered', 'read'
    message_type VARCHAR(50) DEFAULT 'text', -- e.g., 'text', 'image', 'file'
    parent_message_id INT NULL, -- For replies or threading

    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (parent_message_id) REFERENCES messages(id) ON DELETE SET NULL
);

-- Now add the foreign key to messages table for last_read_message_id
ALTER TABLE conversation_participants
ADD CONSTRAINT fk_last_read_message
FOREIGN KEY (last_read_message_id) REFERENCES messages(id) ON DELETE SET NULL;

-- Indexes for performance
CREATE INDEX idx_conversations_last_message_at ON conversations(last_message_at DESC);
CREATE INDEX idx_participants_user_id ON conversation_participants(user_id);
CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_timestamp ON messages(timestamp DESC);
