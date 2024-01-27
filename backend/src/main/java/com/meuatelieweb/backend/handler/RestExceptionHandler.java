package com.meuatelieweb.backend.handler;

import com.meuatelieweb.backend.handler.dto.MethodArgumentNotValidExceptionDetails;
import com.meuatelieweb.backend.handler.dto.ExceptionDetails;
import com.meuatelieweb.backend.handler.dto.InvalidField;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MethodArgumentNotValidExceptionDetails> handleConstraintViolationException(MethodArgumentNotValidException e) {

        MethodArgumentNotValidExceptionDetails responseBody = new MethodArgumentNotValidExceptionDetails(
                "Method Argument Not Valid Exception",
                HttpStatus.BAD_REQUEST.value(),
                "There are invalid fields",
                e.getFieldErrors()
                        .stream()
                        .map(fieldError -> new InvalidField(
                                fieldError.getField(),
                                fieldError.getDefaultMessage()))
                        .toList()
        );

        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ExceptionDetails> handleDuplicateKeyException(DuplicateKeyException e) {

        ExceptionDetails responseBody = new ExceptionDetails(
                "Duplicate Key Exception",
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );

        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDetails> handleInvalidPhoneNumberException(IllegalArgumentException e) {

        ExceptionDetails responseBody = new ExceptionDetails(
                "Illegal Argument Exception",
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );

        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDetails> handleEntityNotFoundException(EntityNotFoundException e) {

        return ResponseEntity.notFound().build();
    }
}
