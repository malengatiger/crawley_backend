package com.boha.crawley;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.logging.Logger;

@ControllerAdvice
@RestController
@Order(1)
public class GlobalExceptionHandler {
    static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getSimpleName());
    static final String mm = "\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F" +
            " GlobalExceptionHandler:  \uD83D\uDD34 \uD83D\uDD34 \uD83D\uDD34";

    public GlobalExceptionHandler() {
        logger.info(mm + " .... GlobalExceptionHandler activated!");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadRequestException(NoHandlerFoundException ex) {
        String errorMessage = mm+" Bad Request intercepted: " + ex.getMessage();
        logger.info(mm + errorMessage);
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
