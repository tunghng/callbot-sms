package com.example.callbotsms.service;


import com.im.sso.dto.model.AppUserDto;
import com.im.sso.dto.request.ChangePasswordRequest;

import java.util.UUID;

public interface UserCredentialsService {

    boolean isEnabled(UUID userId);

    void changePassword(AppUserDto currentUser, ChangePasswordRequest passwordRequest);

    void setPassword(UUID userId, String password);

    void setPassword(UUID userId);
}
