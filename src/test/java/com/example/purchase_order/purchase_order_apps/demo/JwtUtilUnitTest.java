package com.example.purchase_order.purchase_order_apps.demo;

import com.example.purchase_order.purchase_order_apps.config.jwt.JwtUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilUnitTest {
    @Test
    void generate_and_parse_token_ok() {
        JwtUtil util = new JwtUtil("a-very-strong-secret-key-change-me-32bytes", 8);
        String token = util.generateToken(123L, "user@mail.com", Map.of("firstName","A","lastName","B"));

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);

        Long sub = util.parseUserId(token);
        assertEquals(123L, sub);

        assertEquals("A", util.parseClaim(token, "firstName"));
        assertEquals("B", util.parseClaim(token, "lastName"));
        assertEquals("user@mail.com", util.parseClaim(token, "email"));
    }
}

