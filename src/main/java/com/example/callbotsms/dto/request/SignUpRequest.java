package com.example.callbotsms.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class SignUpRequest {

    @NotEmpty(message = "First name may not be empty")
    private String firstName;

    @NotEmpty(message = "Last name may not be empty")
    private String lastName;

    @Email
    @NotEmpty(message = "Email may not be empty")
    private String email;

    @NotEmpty(message = "Password may not be empty")
    private String password;
}
