package com.example.purchase_order.purchase_order_apps.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component // ADDED
public class JwtUtil {

    private final SecretKey key; // ADDED
    private final long expMs;    // ADDED

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.exp-hours}") long expHours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMs = expHours * 60L * 60L * 1000L;
        System.out.println("[DBG] JWT expMs=" + this.expMs + ", secretLen=" + secret.length()); // DEBUG
    }

    public String generateToken(Long userId, String email, Map<String,Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public Long parseUserId(String token) {
        String sub = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
        return Long.valueOf(sub);
    }

    public String parseClaim(String token, String name) {
        Object v = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get(name);
        return v == null ? null : v.toString();
    }
}
