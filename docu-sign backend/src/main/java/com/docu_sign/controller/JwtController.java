package com.docu_sign.controller;


import com.docu_sign.entity.User;
import com.docu_sign.repo.UserRepository;
import com.docu_sign.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtController {

        private final JwtService jwtService;
        private final UserRepository userRepository;

        @GetMapping("/test-token")
        public String testToken() {

            User user = userRepository
                    .findByEmail("parv6@gmail.com")
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            return jwtService.generateToken(user);
        }
    }

