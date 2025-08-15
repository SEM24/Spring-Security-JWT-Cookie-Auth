package com.security.fiverr.auth.service;

import com.security.fiverr.auth.model.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {
    @Transactional
    AuthResult register(RegisterRequest request);

    @Transactional
    AuthResult login(LoginRequest request);

    @Transactional
    AuthResult refreshAccessToken(HttpServletRequest request);

    @Transactional
    LogoutResponse logout(HttpServletRequest request);
}
