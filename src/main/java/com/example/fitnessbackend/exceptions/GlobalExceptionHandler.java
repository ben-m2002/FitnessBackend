package com.example.fitnessbackend.exceptions;


import com.example.fitnessbackend.dtos.responses.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ResponseDto> handleInvalidJwtException(InvalidJwtException ex) {
        return ResponseEntity.status(401).body(new ResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(new ResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExists.class)
    public ResponseEntity<ResponseDto> handleEmailAlreadyExists(EmailAlreadyExists ex) {
        return ResponseEntity.status(422).body(new ResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(UsernameAlreadyExists.class)
    public ResponseEntity<ResponseDto> handleUsernameAlreadyExists(UsernameAlreadyExists ex) {
        return ResponseEntity.status(422).body(new ResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseDto> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ResponseEntity.status(401).body(new ResponseDto(ex.getMessage()));
    }
}
