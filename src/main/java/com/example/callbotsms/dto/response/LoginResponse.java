package com.example.callbotsms.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private static final long serialVersionUID = -8091879091924046844L;

    @JsonProperty(value = "token")
    private String token;

    @JsonProperty(value = "refreshToken")
    private String refreshToken;

    @JsonProperty(value = "expiresIn")
    private long expiresIn;
}
