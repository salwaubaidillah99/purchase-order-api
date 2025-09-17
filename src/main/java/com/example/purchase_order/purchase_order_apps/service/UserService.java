package com.example.purchase_order.purchase_order_apps.service;

import com.example.purchase_order.purchase_order_apps.config.jwt.JwtUtil; // CHANGED: bean
import com.example.purchase_order.purchase_order_apps.config.jwt.PasswordUtil;
import com.example.purchase_order.purchase_order_apps.entity.User;
import com.example.purchase_order.purchase_order_apps.entity.dto.*;
import com.example.purchase_order.purchase_order_apps.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map; // ADDED
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil; // ADDED

    public UserService(UserRepository userRepo, JwtUtil jwtUtil) { // ADDED
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    public List<User> getAll() { return userRepo.findAll(); }
    public Optional<User> getById(Long id) { return userRepo.findById(id); }

    @Transactional
    public UserResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already registered");
        if (userRepo.existsByFirstNameAndLastName(req.getFirstName(), req.getLastName()))
            throw new RuntimeException("User name already exists");
        User u = new User();
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        u.setPassword(PasswordUtil.hash(req.getPassword()));
        u.setCreatedBy("system");
        u.setUpdatedBy("system");
        u.setCreatedDatetime(LocalDateTime.now());
        u.setUpdatedDatetime(LocalDateTime.now());
        User saved = userRepo.save(u);
        return new UserResponse(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getEmail(), saved.getPhone());
    }

    public AuthResponse login(LoginRequest req) {
        User u = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!u.getPassword().equals(PasswordUtil.hash(req.getPassword())))
            throw new RuntimeException("Invalid credentials");

        Map<String,Object> claims = new HashMap<>();
        claims.put("firstName", u.getFirstName());
        claims.put("lastName", u.getLastName());

        System.out.println("[DBG] LOGIN userId=" + u.getId() + ", email=" + u.getEmail()); // DEBUG
        String token = jwtUtil.generateToken(u.getId(), u.getEmail(), claims); // CHANGED
        System.out.println("[DBG] ISSUED TOKEN LEN=" + token.length()); // DEBUG

        return new AuthResponse(token, u.getId(), u.getFirstName(), u.getLastName(), u.getEmail());
    }

    @Transactional
    public UserResponse update(Long id, RegisterRequest req, String audit) {
        User u = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (!u.getEmail().equals(req.getEmail()) && userRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already in use");
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        if (req.getPassword()!=null && !req.getPassword().isBlank())
            u.setPassword(PasswordUtil.hash(req.getPassword()));
        u.setUpdatedBy(audit == null ? "system" : audit);
        u.setUpdatedDatetime(LocalDateTime.now());
        return new UserResponse(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getPhone());
    }

    public void delete(Long id) { userRepo.deleteById(id); }
}
