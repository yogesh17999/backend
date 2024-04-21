package org.backend.service;

import org.backend.dto.SignupRequestDto;
import org.backend.dto.UserDto;

import java.util.List;

public interface UserTransectionService {
    List<UserDto> getAllUser();

    UserDto getUserByID(Long id);

    String updateUser(Long id, SignupRequestDto signupRequestDto);

    String deleteUser(Long id);
}
