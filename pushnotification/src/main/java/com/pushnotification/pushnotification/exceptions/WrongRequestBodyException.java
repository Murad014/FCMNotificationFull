package com.pushnotification.pushnotification.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class WrongRequestBodyException extends RuntimeException {
    private final String message;

    public WrongRequestBodyException(String message) {
        super(message);
        this.message = message;
    }

}
