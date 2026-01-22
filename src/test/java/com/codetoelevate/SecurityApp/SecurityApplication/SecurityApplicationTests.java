package com.codetoelevate.SecurityApp.SecurityApplication;

import com.codetoelevate.SecurityApp.SecurityApplication.entities.User;
import com.codetoelevate.SecurityApp.SecurityApplication.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurityApplicationTests {

    @Autowired
    private JwtService jwtService;
	@Test
	void contextLoads() {

        User user = new User(5L, "rahul@gmail.com", "Rahul123");
        String token = jwtService.generateAccessToken(user);

        System.out.println("Token :"+token);

        Long id = jwtService.getUserIdFromToken(token);
        System.out.println("Id : "+id);

	}
}
