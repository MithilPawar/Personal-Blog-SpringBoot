package com.blog.personal_blog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // REVIEW NOTE: Keep one uniform payload shape for all errors.
    private Map<String, Object> buildErrorBody(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return response;
    }

//    Handle validation error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error ->{
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = buildErrorBody(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                "Request validation failed"
        );
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

//    Handling IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex){
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

//    catch all other exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex){
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

//    User not found exception
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex){
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.NOT_FOUND, "User not found", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    //Blog not found Exception
    @ExceptionHandler(BlogNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBlogNotFound(BlogNotFoundException ex){
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.NOT_FOUND, "Blog not found", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return new ResponseEntity<>(
                buildErrorBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}
