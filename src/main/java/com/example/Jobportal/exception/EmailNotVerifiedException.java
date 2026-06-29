package com.example.Jobportal.exception;

import lombok.Getter;

@Getter
public class EmailNotVerifiedException extends RuntimeException {
    private final String email;

    public EmailNotVerifiedException(String email) {
        super("Please verify your email first. A new code has been sent.");
        this.email = email;
    }
}