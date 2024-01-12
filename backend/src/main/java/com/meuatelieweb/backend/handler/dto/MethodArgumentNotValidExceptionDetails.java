package com.meuatelieweb.backend.handler.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MethodArgumentNotValidExceptionDetails extends ExceptionDetails {

    protected List<InvalidField> invalidFields;

    public MethodArgumentNotValidExceptionDetails(
            String title,
            int status,
            String details,
            List<InvalidField> invalidFields) {
        super(title, status, details);
        this.invalidFields = invalidFields;
    }
}