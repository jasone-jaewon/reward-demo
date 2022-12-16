package com.example.rewarddemo.error;

import com.example.rewarddemo.error.exception.BadRequestException;
import com.example.rewarddemo.error.exception.ConflictException;
import com.example.rewarddemo.error.exception.NotFoundException;
import com.example.rewarddemo.error.exception.UnAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, BadRequestException.class})
    public ResponseEntity<Error> handleBadRequest(RuntimeException exception) {
        String message = exception.getMessage();
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        Error error = new Error(badRequest, message);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Error> handleNotFound(RuntimeException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        Error error = new Error(status, message);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler({UnAuthorizedException.class})
    public ResponseEntity<Error> handleUnAuthorized(RuntimeException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Error error = new Error(status, message);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<Error> handleConflict(RuntimeException exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;
        Error error = new Error(status, message);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Error> handleUnhandledException(Exception exception) {
        String message = exception.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Error error = new Error(status, message);
        return ResponseEntity.internalServerError().body(error);
    }
}
