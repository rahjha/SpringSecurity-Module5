package com.codetoelevate.SecurityApp.SecurityApplication.services;

import com.codetoelevate.SecurityApp.SecurityApplication.dto.SignupDto;
import com.codetoelevate.SecurityApp.SecurityApplication.dto.UserDto;
import com.codetoelevate.SecurityApp.SecurityApplication.entities.User;
import com.codetoelevate.SecurityApp.SecurityApplication.exceptions.ResourceNotFoundException;
import com.codetoelevate.SecurityApp.SecurityApplication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(()-> new BadCredentialsException("User with email "+username+" not found"));
    }

    public User getUserById(Long userId){
        return userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found with id :"+userId));
    }

    public UserDto signup(SignupDto signupDto) {
        Optional<User> user = userRepository.findByEmail(signupDto.getEmail());
        if(user.isPresent()){
            throw new BadCredentialsException("User already present with email :"+signupDto.getEmail());
        }
        User userToBeSaved = modelMapper.map(signupDto, User.class);
        userToBeSaved.setPassword(passwordEncoder.encode(userToBeSaved.getPassword()));
        User createduser = userRepository.save(userToBeSaved);
        return modelMapper.map(createduser, UserDto.class);
    }
}