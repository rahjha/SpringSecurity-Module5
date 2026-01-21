package com.codetoelevate.SecurityApp.SecurityApplication.services;

import com.codetoelevate.SecurityApp.SecurityApplication.dto.LoginDto;
import com.codetoelevate.SecurityApp.SecurityApplication.entities.SessionEntity;
import com.codetoelevate.SecurityApp.SecurityApplication.entities.User;
import com.codetoelevate.SecurityApp.SecurityApplication.repositories.SessionEntityRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionEntityRepository sessionEntityRepository;

    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token =  jwtService.generateToken(user);
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
        sessionEntityRepository.save(new SessionEntity(user.getId(), token));
        return token;
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
}
