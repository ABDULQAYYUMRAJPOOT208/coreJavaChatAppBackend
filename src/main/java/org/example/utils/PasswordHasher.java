package org.example.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    public static String hashedPassword(String password)
    {
        return org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt(12));
    }
    public static boolean verifyPassword(String pass, String hash)
    {
        return BCrypt.checkpw(pass,hash);
    }
}
