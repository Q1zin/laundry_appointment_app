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
            return new LoginResponse(false, "Пользователь не найден", null, null);
        }

        // Проверить блокировку
        if (user.isBlocked()) {
            return new LoginResponse(false, "Пользователь заблокирован", null, null);
        }

        // Шаг 2: Проверить пароль
        if (!verifyPassword(user, request.getPassword())) {
            return new LoginResponse(false, "Неверный пароль", null, null);
        }

        // Шаг 3: Сгенерировать JWT токен
        String token = jwtUtil.generateToken(user.getName(), user.getRole());

        return new LoginResponse(true, "Вход выполнен успешно", token, user.getRole());
    }

    /**
     * Регистрация нового пользователя
     */
    public LoginResponse register(String username, String password, String email, String fullName, String room, String contract) {
        // Проверка существования пользователя
        if (userRepository.findByName(username).isPresent()) {
            return new LoginResponse(false, "Пользователь с таким логином уже существует", null, null);
        }

        // Создание нового пользователя
        User newUser = new User();
        newUser.setName(username);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setRole("user");
        newUser.setIsBlocked(false);
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setRoom(room);
        newUser.setContract(contract);

        userRepository.save(newUser);

        // Генерация токена для автоматического входа
        String token = jwtUtil.generateToken(newUser.getName(), newUser.getRole());

        return new LoginResponse(true, "Registration successful", token, newUser.getRole());
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
