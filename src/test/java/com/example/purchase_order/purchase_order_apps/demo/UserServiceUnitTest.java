package com.example.purchase_order.purchase_order_apps.demo;

import com.example.purchase_order.purchase_order_apps.config.jwt.JwtUtil;
import com.example.purchase_order.purchase_order_apps.config.jwt.PasswordUtil;
import com.example.purchase_order.purchase_order_apps.entity.User;
import com.example.purchase_order.purchase_order_apps.entity.dto.AuthResponse;
import com.example.purchase_order.purchase_order_apps.entity.dto.LoginRequest;
import com.example.purchase_order.purchase_order_apps.repository.UserRepository;
import com.example.purchase_order.purchase_order_apps.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class UserServiceUnitTest {
    @Test
    void login_ok_returns_token_with_sub() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        JwtUtil jwt = new JwtUtil("a-very-strong-secret-key-change-me-32bytes", 8);
        UserService service = new UserService(repo, jwt);

        User u = new User();
        u.setId(5L);
        u.setFirstName("A");
        u.setLastName("B");
        u.setEmail("a@b.com");
        u.setPassword(PasswordUtil.hash("secret"));
        when(repo.findByEmail("a@b.com")).thenReturn(Optional.of(u));

        LoginRequest req = new LoginRequest();
        req.setEmail("a@b.com");
        req.setPassword("secret");

        AuthResponse resp = service.login(req);
        assertNotNull(resp.getToken());
        assertEquals(5L, jwt.parseUserId(resp.getToken()));
        assertEquals("A", jwt.parseClaim(resp.getToken(),"firstName"));
    }
}
