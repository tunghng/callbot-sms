package com.example.callbotsms.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordRequest {

    @NotEmpty(message = "old password may not be empty")
    @Size(min = 8, max = 255)
    private String oldPassword;

    @NotEmpty(message = "new password may not be empty")
    @Size(min = 8, max = 255)
    private String newPassword;

}
