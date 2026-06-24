package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JWTUtil {

    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private static final String ISSUER = "chatApp";
    private static final long EXPIRATION_TIME_MS = 3600000;

    public JWTUtil() {
        String jwtSecret = "JWTSECRET";

        this.algorithm = Algorithm.HMAC256(jwtSecret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
    }

    public String generateToken(long id) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(String.valueOf(id))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .withClaim("role", "user")
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) {
        try {
            return jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            System.err.println("Token verification failed: " + e.getMessage());
            return null;
        }
    }
}
