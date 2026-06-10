package com.docu_sign.service.serviceimpl;

import com.docu_sign.entity.User;
import com.docu_sign.repo.UserRepository;
import com.docu_sign.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {


    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
