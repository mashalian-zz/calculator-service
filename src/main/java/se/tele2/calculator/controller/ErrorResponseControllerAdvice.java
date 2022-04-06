package se.tele2.calculator.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import se.tele2.calculator.exception.EmptyInputsException;
import se.tele2.calculator.exception.ErrorResponse;
import se.tele2.calculator.exception.NotFoundException;

@ControllerAdvice
public class ErrorResponseControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ErrorResponse> handleArithmeticException(ArithmeticException e){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    @ExceptionHandler(EmptyInputsException.class)
    public ResponseEntity<ErrorResponse> handleEmptyInputsException(EmptyInputsException e){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }
}
