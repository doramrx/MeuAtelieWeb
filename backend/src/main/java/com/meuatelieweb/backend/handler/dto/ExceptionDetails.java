package com.meuatelieweb.backend.handler.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ExceptionDetails {
    protected String title;
    protected int status;
    protected String details;
    protected LocalDateTime timestamp;

    public ExceptionDetails(String title, int status, String details) {
        this.title = title;
        this.status = status;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
