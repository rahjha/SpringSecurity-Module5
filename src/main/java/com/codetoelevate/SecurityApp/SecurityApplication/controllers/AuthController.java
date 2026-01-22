package com.codetoelevate.SecurityApp.SecurityApplication.controllers;

import com.codetoelevate.SecurityApp.SecurityApplication.dto.LoginDto;
import com.codetoelevate.SecurityApp.SecurityApplication.dto.LoginResponseDto;
import com.codetoelevate.SecurityApp.SecurityApplication.dto.SignupDto;
import com.codetoelevate.SecurityApp.SecurityApplication.dto.UserDto;
import com.codetoelevate.SecurityApp.SecurityApplication.services.AuthService;
import com.codetoelevate.SecurityApp.SecurityApplication.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping(path="/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupDto signupDto){
        UserDto userDto = userService.signup(signupDto);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping(path="/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response){
        LoginResponseDto loginResponseDto = authService.login(loginDto);

        Cookie cookie = new Cookie("refreshToken", loginResponseDto.getRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(loginResponseDto.getAccessToken());
    }

    @PostMapping(path="/logout")
    public ResponseEntity<String> logout(){
        authService.logout();
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping(path="/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request){
        LoginResponseDto loginResponseDto = authService.refresh(request);
        return ResponseEntity.ok(loginResponseDto.getAccessToken());
    }
}
