package com.docu_sign.service.serviceimpl;

import com.docu_sign.dto.LoginResponse;
import com.docu_sign.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.docu_sign.dto.LoginRequest;
import com.docu_sign.dto.RegisterRequest;
import com.docu_sign.entity.Role;
import com.docu_sign.entity.User;
import com.docu_sign.repo.UserRepository;
import com.docu_sign.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
        throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
            .fullName(request.fullName())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.USER)
            .build();

        userRepository.save(user);

       
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                                    .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches( request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
       
    }
    
}
