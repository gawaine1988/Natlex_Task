package org.example.natlex_task.adapter.exception;


import org.example.natlex_task.adapter.dto.ApiResponse;
import org.example.natlex_task.application.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ApiResponse.buildResponse(HttpStatus.BAD_REQUEST, errors.toString(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse handleValidationExceptions(ResourceNotFoundException ex) {
        return ApiResponse.buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(ArgumentNotValidException.class)
    public ApiResponse handleValidationExceptions(ArgumentNotValidException ex) {
        return ApiResponse.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }
}
