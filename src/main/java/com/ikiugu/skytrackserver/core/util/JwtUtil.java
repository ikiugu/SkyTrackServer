package com.ikiugu.skytrackserver.core.util;

import com.ikiugu.skytrackserver.core.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  private final SecretKey secretKey;
  private static final long EXPIRATION_TIME = 3600000; // 1 hour in milliseconds

  public JwtUtil(
      @Value("${JWT_SECRET:default-secret-key-change-in-production-minimum-256-bits}")
          String jwtSecret) {
    this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  public String issueToken(UUID userId, User.UserRole role) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

    return Jwts.builder()
        .subject(userId.toString())
        .claim("role", role.name())
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(secretKey)
        .compact();
  }

  public Claims validateToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  public UUID extractUserId(String token) {
    Claims claims = validateToken(token);
    return UUID.fromString(claims.getSubject());
  }

  public String extractRole(String token) {
    Claims claims = validateToken(token);
    return claims.get("role", String.class);
  }
}
