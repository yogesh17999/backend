package org.backend.service;

import org.backend.dto.LoginRequestDto;
import org.backend.dto.LoginResponseDto;
import org.backend.dto.SignupRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    LoginResponseDto authenticateUser(LoginRequestDto loginRequestDto);

    String addUser(SignupRequestDto signUpRequest);
}
