package com.agata.signuplogin.controller;

import com.agata.signuplogin.dto.LoginRequest;
import com.agata.signuplogin.model.User;
import com.agata.signuplogin.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public String login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials!"));

        if (user.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account blocked!");
        }

        System.out.println("Senha enviada: " + request.getPassword());
        System.out.println("Senha do banco: " + user.getPassword());

        if (!request.getPassword().equals(user.getPassword())) {
            user.setChances(user.getChances() - 1);
            if (user.getChances() == 0) {
                user.setBlocked(true);
                userRepository.save(user);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for blocked account!");
            }
            userRepository.save(user);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid password. Remaining chances: " + user.getChances());
        }

        user.setChances(3);
        userRepository.save(user);
        return "Access allowed!";
    }
}
