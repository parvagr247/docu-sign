package com.docu_sign.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docu_sign.dto.LoginRequest;
import com.docu_sign.dto.RegisterRequest;
import com.docu_sign.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register( @Valid @RequestBody RegisterRequest request ) {
    
        userService.register(request);
        return ResponseEntity.ok("User registered successfully");

    }

    @PostMapping("/login")
    public ResponseEntity<String> login( @Valid @RequestBody LoginRequest request ) {

        return ResponseEntity.ok(userService.login(request));
}
    
}
