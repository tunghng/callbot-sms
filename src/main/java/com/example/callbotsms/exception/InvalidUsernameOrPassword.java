package com.example.callbotsms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidUsernameOrPassword extends UnAuthorizedException {
    private static final long serialVersionUID = 1L;

    public InvalidUsernameOrPassword(String message) {
        super(message);
    }
}
