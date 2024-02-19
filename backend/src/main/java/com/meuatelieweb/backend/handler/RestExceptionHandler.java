package com.meuatelieweb.backend.handler;

import com.meuatelieweb.backend.handler.dto.MethodArgumentNotValidExceptionDetails;
import com.meuatelieweb.backend.handler.dto.ExceptionDetails;
import com.meuatelieweb.backend.handler.dto.InvalidField;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class RestExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MethodArgumentNotValidExceptionDetails> handleConstraintViolationException(MethodArgumentNotValidException e) {

        MethodArgumentNotValidExceptionDetails responseBody = new MethodArgumentNotValidExceptionDetails(
                "Method Argument Not Valid Exception",
                HttpStatus.BAD_REQUEST.value(),
                this.messageSource.getMessage("shared.error.constraintViolationDetail", null, Locale.getDefault()),
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
        ExceptionDetails responseBody = new ExceptionDetails(
                "Entity Not Found Exception",
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );

        return ResponseEntity.badRequest().body(responseBody);
    }
}
