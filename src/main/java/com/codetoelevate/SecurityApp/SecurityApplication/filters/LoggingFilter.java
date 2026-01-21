package com.codetoelevate.SecurityApp.SecurityApplication.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        try{
            log.info(
                    "Incoming request -> method={}, uri={}, remoteAddress={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr()
            );
            filterChain.doFilter(request, response);
        }catch(Exception ex){
            log.error("Exception occurred while logging the request/response :",ex);
            throw ex;
        }finally {
            long duration = System.currentTimeMillis()-startTime;
            log.info(
                    "Outgoing response -> method={}, uri={}, remoteAddress={}, time={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    duration
            );
        }
    }
}