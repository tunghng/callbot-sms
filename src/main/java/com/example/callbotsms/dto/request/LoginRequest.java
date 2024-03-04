package com.example.callbotsms.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotEmpty(message = "Email may not be empty")
    private String email;

    @NotEmpty(message = "Password may not be empty")
    private String password;
}
