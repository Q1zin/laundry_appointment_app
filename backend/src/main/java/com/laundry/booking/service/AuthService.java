package com.laundry.booking.service;

import com.laundry.booking.dto.LoginRequest;
import com.laundry.booking.dto.LoginResponse;
import com.laundry.booking.entity.User;
import com.laundry.booking.repository.UserRepository;
import com.laundry.booking.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Auth Controller - login method
     * Последовательность вызовов:
     * 1. Найти пользователя по username
     * 2. Проверить пароль
     * 3. Вернуть результат
     */
    public LoginResponse login(LoginRequest request) {
        // Шаг 1: Найти пользователя
        User user = userRepository.findByName(request.getUsername())
                .orElse(null);
        
        if (user == null) {
            return new LoginResponse(false, "User not found", null, null);
        }

        // Проверить блокировку
        if (user.isBlocked()) {
            return new LoginResponse(false, "User is blocked", null, null);
        }

        // Шаг 2: Проверить пароль
        if (!verifyPassword(user, request.getPassword())) {
            return new LoginResponse(false, "Invalid password", null, null);
        }

        // Шаг 3: Сгенерировать JWT токен
        String token = jwtUtil.generateToken(user.getName(), user.getRole());

        return new LoginResponse(true, "Login successful", token, user.getRole());
    }

    /**
     * Получить пользователя по имени
     */
    public User getUser(String username) {
        return userRepository.findByName(username).orElse(null);
    }

    /**
     * Проверка пароля
     */
    private boolean verifyPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
}
