package com.example.localservice.service;

import com.example.localservice.dto.AuthResponseDto;
import com.example.localservice.dto.LoginDto;
import com.example.localservice.dto.RegistrationDto;

public interface AuthService {
    AuthResponseDto register(RegistrationDto request);
    AuthResponseDto login(LoginDto request);
}
