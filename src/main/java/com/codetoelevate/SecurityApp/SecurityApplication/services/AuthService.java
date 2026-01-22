package com.codetoelevate.SecurityApp.SecurityApplication.services;

import com.codetoelevate.SecurityApp.SecurityApplication.dto.LoginDto;
import com.codetoelevate.SecurityApp.SecurityApplication.dto.LoginResponseDto;
import com.codetoelevate.SecurityApp.SecurityApplication.entities.SessionEntity;
import com.codetoelevate.SecurityApp.SecurityApplication.entities.User;
import com.codetoelevate.SecurityApp.SecurityApplication.exceptions.ResourceNotFoundException;
import com.codetoelevate.SecurityApp.SecurityApplication.repositories.SessionEntityRepository;
import com.codetoelevate.SecurityApp.SecurityApplication.repositories.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionEntityRepository sessionEntityRepository;
    private final UserRepository userRepository;

    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String accessToken =  jwtService.generateAccessToken(user);
        String refreshToken =  jwtService.generateRefreshToken(user);
        /*after the token is created, do a lookup inside SessionEntity table and validate if entry already exist
        for this user, then expire the old token and insert a new entry with new token*/

        /*boolean isValid = sessionEntityRepository.existsByUserId(user.getId());
        SessionEntity sessionEntity = new SessionEntity(user.getId(), token);
        if(!isValid){
            //if no valid entry present then insert a new entry
            sessionEntityRepository.save(sessionEntity);
        }
        else{
            //if an entry is already present and the token is also valid, still expire the existing token and
            // create a new token entry, we are making sure only one user logs-in at a time
            sessionEntityRepository.deleteByUserId(user.getId());
            sessionEntityRepository.save(sessionEntity);
        }*/
        //just calling the save method will replace the existing entry, so no need to do multiple DB calls
        sessionEntityRepository.save(new SessionEntity(user.getId(), refreshToken));
        return new LoginResponseDto(user.getId(), accessToken, refreshToken);
    }

    public void logout() {
        //get the already loggedin User
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null || !authentication.isAuthenticated()){
            throw new JwtException("User is not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if(!(principal instanceof User)){
            throw new JwtException("Invalid authentication principal");
        }

        User user = (User) principal;
        //delete from the SessionEntity
        sessionEntityRepository.deleteByUserId(user.getId());
        SecurityContextHolder.clearContext();
    }

    public LoginResponseDto refresh(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = Arrays.stream(cookies)
                .filter(cookie->"refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        //now get the userId from this refreshToken, then fetch the refreshToken from SessionEntity DB for userId,
        //validate that refresh token is valid
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId).orElseThrow(()->new JwtException("no user found for the userId"));
        String accessToken =  jwtService.generateAccessToken(user);
        String newRefreshToken =  jwtService.generateRefreshToken(user);
        //update the refresh token inside SessionEntity DB
        sessionEntityRepository.save(new SessionEntity(userId, newRefreshToken));
        return new LoginResponseDto(userId,accessToken, newRefreshToken);
    }
}
