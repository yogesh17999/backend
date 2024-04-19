package org.backend.serviceImpl;

import jakarta.transaction.Transactional;
import org.backend.dto.LoginRequestDto;
import org.backend.dto.LoginResponseDto;
import org.backend.dto.SignupRequestDto;
import org.backend.entity.User;
import org.backend.entity.UserRoles;
import org.backend.exceptions.UserValidationException;
import org.backend.repository.UserRepository;
import org.backend.service.UserService;
import org.backend.util.Common;
import org.backend.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public LoginResponseDto authenticateUser(LoginRequestDto loginRequestDto) {
        Authentication authentication
                =authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),loginRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return LoginResponseDto.builder().token(jwtUtil.generateJwtToken(authentication)).build();
    }

    @Override
    public String addUser(SignupRequestDto signUpRequest) {
        if (Boolean.FALSE.equals(Common.isValidEmail(signUpRequest.getEmail())))
            throw new UserValidationException("Please enter a valid email address");
        if (Boolean.FALSE.equals(Common.isValidPassword(signUpRequest.getPassword())))
            throw new UserValidationException("Please enter a valid password");
        userRepository.findByEmail(signUpRequest.getEmail()).ifPresent(user->{ throw new UserValidationException("User is already exist");}) ;

        User userEntity = modelMapper.map(signUpRequest, User.class);
        userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        UserRoles userRoles = new UserRoles();
        userRoles.setRoleName(signUpRequest.getRole().getRoleName().toUpperCase());
        userEntity.setRole(userRoles);
        userRepository.save(userEntity);
        return "User Successfully added";
    }
}
