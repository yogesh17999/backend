package org.backend.serviceImpl;

import org.backend.dto.SignupRequestDto;
import org.backend.dto.UserDto;
import org.backend.entity.User;
import org.backend.repository.UserRepository;
import org.backend.service.UserTransectionService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserTransectionServiceImpl implements UserTransectionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public List<UserDto> getAllUser() {
       return modelMapper.map(userRepository.findAll(),new TypeToken<List<UserDto>>(){}.getType());
    }

    @Override
    public UserDto getUserByID(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter correct user id"));
        return  modelMapper.map(user,UserDto.class);
    }

    @Override
    public String updateUser(Long id, SignupRequestDto signupRequestDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter correct user id"));
        user.setEmail(signupRequestDto.getEmail());
        user.setPhoneNumber(signupRequestDto.getPhoneNumber());
        userRepository.save(user);
        return "Successfully updated";
    }

    @Override
    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        return  "Successfully deleted";
    }
}
