package com.agata.signuplogin.controller;

import com.agata.signuplogin.model.User;
import com.agata.signuplogin.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/signup")
public class SignUpController {

    private final UserRepository userRepository;

    public SignUpController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User signUp(@Valid @RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists!");
        }
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> listAll() {
        return userRepository.findAll();
    }
}
