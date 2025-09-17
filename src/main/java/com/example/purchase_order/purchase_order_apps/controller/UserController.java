package com.example.purchase_order.purchase_order_apps.controller;


import com.example.purchase_order.purchase_order_apps.entity.dto.*;
import com.example.purchase_order.purchase_order_apps.response.ApiResponse;
import com.example.purchase_order.purchase_order_apps.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService us;

    public UserController(UserService us) { this.us = us; }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest req) {
        var resp = us.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(201, "Registered", resp));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest req) {
        var resp = us.login(req);
        return ResponseEntity.ok(new ApiResponse(200, "Login success", resp));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> get(@PathVariable Long id) {
        return us.getById(id)
                .map(u -> new UserResponse(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getPhone()))
                .map(r -> ResponseEntity.ok(new ApiResponse(200, "Success", r)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "User not found", null)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id,
                                              @RequestBody @Valid RegisterRequest req,
                                              HttpServletRequest http) {
        String audit = (String) http.getAttribute("authAuditName");
        var resp = us.update(id, req, audit);
        return ResponseEntity.ok(new ApiResponse(200, "User updated", resp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        us.delete(id);
        return ResponseEntity.ok(new ApiResponse(200, "User deleted", null));
    }
}