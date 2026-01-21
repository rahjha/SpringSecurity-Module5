package com.codetoelevate.SecurityApp.SecurityApplication.advice;

import com.codetoelevate.SecurityApp.SecurityApplication.exceptions.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleResourceNotFoundError(ResourceNotFoundException exception){
        ApiError error = new ApiError(exception.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> authenticationError(AuthenticationException exception){
        ApiError error = new ApiError(exception.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleJwtExcetion(JwtException ex){
        ApiError error = new ApiError(ex.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
