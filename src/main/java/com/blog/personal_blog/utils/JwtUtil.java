package com.blog.personal_blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private SecretKey key;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 hours

    @PostConstruct
    public void init() {
        this.key = buildKey();
    }

    /* ===================== TOKEN GENERATION ===================== */

    public String generateToken(String username, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getKey())
                .compact();
    }

    /* ===================== KEY ===================== */

    private SecretKey getKey() {
        return key;
    }

    private SecretKey buildKey() {
        byte[] keyBytes = resolveSecretBytes(SECRET_KEY);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "Invalid jwt.secret/JWT_SECRET: key must be at least 32 bytes (256 bits) for HS256. " +
                            "Use a longer secret or a Base64-encoded 32+ byte key."
            );
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] resolveSecretBytes(String rawSecret) {
        String secret = rawSecret == null ? "" : rawSecret.trim();
        if (secret.isEmpty()) {
            throw new IllegalStateException("Missing jwt.secret/JWT_SECRET configuration.");
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(secret);
            if (decoded.length >= 32) {
                return decoded;
            }
        } catch (IllegalArgumentException ignored) {
        }

        return secret.getBytes(StandardCharsets.UTF_8);
    }

    /* ===================== EXTRACTION ===================== */

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
    }

    private <T> T extractClaims(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /* ===================== VALIDATION ===================== */

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}
