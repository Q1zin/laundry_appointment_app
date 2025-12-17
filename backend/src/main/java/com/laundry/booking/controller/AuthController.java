package com.laundry.booking.controller;

import com.laundry.booking.dto.LoginRequest;
import com.laundry.booking.dto.LoginResponse;
import com.laundry.booking.dto.RegisterRequest;
import com.laundry.booking.entity.User;
import com.laundry.booking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth Controller - Login UI
 * Endpoints:
 * - POST /api/auth/login
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * Body: { username: String, password: String }
     * Response: { success: boolean, message: String, token: String, role: String }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/register
     * Body: { username, password, email, fullName, room, contract }
     * Response: { success: boolean, message: String, token: String, role: String }
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(
            request.getUsername(),
            request.getPassword(),
            request.getEmail(),
            request.getFullName(),
            request.getRoom(),
            request.getContract()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/user/:username
     * Response: User object
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        User user = authService.getUser(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
