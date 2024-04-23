package org.backend.serviceImpl;

import jakarta.transaction.Transactional;
import org.backend.dto.LoginRequestDto;
import org.backend.dto.LoginResponseDto;
import org.backend.dto.SignupRequestDto;
import org.backend.entity.User;
import org.backend.entity.UserRoles;
import org.backend.exceptions.UserValidationException;
import org.backend.repository.UserRepository;
import org.backend.security.context.RequestContext;
import org.backend.service.UserService;
import org.backend.util.Common;
import org.backend.security.helper.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    private final ModelMapper modelMapper;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RequestContext requestContext;

    public UserServiceImpl(ModelMapper modelMapper, JwtUtil jwtUtil, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserRepository userRepository, RequestContext requestContext) {
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.requestContext = requestContext;
    }


    @Override
    public LoginResponseDto authenticateUser(LoginRequestDto loginRequestDto) {
        Authentication authentication
                =authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),loginRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtil.generateJwtToken(authentication);
    }

    @Override
    public String addUser(SignupRequestDto signUpRequest) {
        if (Boolean.FALSE.equals(Common.isValidEmail(signUpRequest.getEmail())))
            throw new UserValidationException("Please enter a valid email address");
        if (Boolean.FALSE.equals(Common.isValidPassword(signUpRequest.getPassword())))
            throw new UserValidationException("Please enter a valid password");
        userRepository.findByEmail(signUpRequest.getEmail()).ifPresent(user->{ throw new UserValidationException("User is already exist");});

        User userEntity = modelMapper.map(signUpRequest, User.class);
        userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        UserRoles userRoles = new UserRoles();
        if(!ObjectUtils.isEmpty(signUpRequest.getRole()))
         userRoles.setRoleName(signUpRequest.getRole().getRoleName().toUpperCase());
        else
            userRoles.setRoleName("USER");
        userEntity.setRole(userRoles);
//        if (!ObjectUtils.isEmpty(requestContext) && !ObjectUtils.isEmpty(requestContext.getRoles()) && requestContext.getRoles().stream().anyMatch(s -> s.equalsIgnoreCase("ROLE_ADMIN"))) {
//            userRepository.findByEmail(requestContext.getPreferredUserName()).ifPresent(user -> userEntity.setChildUser(List.of(user)));
//        }
        userRepository.save(userEntity);
        return "User Successfully added";
    }
}
