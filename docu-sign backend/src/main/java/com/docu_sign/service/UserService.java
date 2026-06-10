package com.docu_sign.service;

import com.docu_sign.dto.LoginRequest;
import com.docu_sign.dto.LoginResponse;
import com.docu_sign.dto.RegisterRequest;

public interface UserService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
    
}
