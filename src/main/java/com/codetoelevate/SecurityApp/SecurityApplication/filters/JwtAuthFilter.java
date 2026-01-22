package com.codetoelevate.SecurityApp.SecurityApplication.filters;

import com.codetoelevate.SecurityApp.SecurityApplication.entities.SessionEntity;
import com.codetoelevate.SecurityApp.SecurityApplication.entities.User;
import com.codetoelevate.SecurityApp.SecurityApplication.repositories.SessionEntityRepository;
import com.codetoelevate.SecurityApp.SecurityApplication.services.JwtService;
import com.codetoelevate.SecurityApp.SecurityApplication.services.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final SessionEntityRepository sessionEntityRepository;
    String tokenFromSession;
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = requestTokenHeader.split("Bearer ")[1];
            Long userId = jwtService.getUserIdFromToken(token);
            log.info("userId inside JwtFilter {}", userId);
            if (userId == null) {
                throw new JwtException("Invalid JWT token");
            }

            /*SessionEntity sessionEntity = sessionEntityRepository.findById(userId).orElseThrow(() -> new JwtException("Session not found"));
            tokenFromSession = sessionEntity.getRefreshToken();
            log.info("tokenFromSession {}", tokenFromSession);
            if(!tokenFromSession.equalsIgnoreCase(token)){
                throw new JwtException("Token revoked or replaced");
            }*/
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserById(userId);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, null);
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}