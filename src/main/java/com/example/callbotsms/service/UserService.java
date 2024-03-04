package com.example.callbotsms.service;

import com.im.sso.dto.model.AppUserDto;
import com.im.sso.dto.request.SignUpRequest;
import com.im.sso.dto.response.UserProfileResponse;
import com.im.sso.dto.response.page.PageData;
import com.im.sso.dto.response.page.PageLink;
import com.im.sso.model.enums.RoleType;

import java.util.UUID;

public interface UserService {

    AppUserDto save(AppUserDto userDto, AppUserDto currentUser);

    AppUserDto signUp(SignUpRequest signUpRequest);

    PageData<?> findUsers(
            PageLink pageLink,
            RoleType role,
            UUID contactId,
            AppUserDto currentUser,
            Long createdAtStartTs,
            Long createdAtEndTs,
            Boolean isEnabled,
            Boolean isSearchMatchCase
    );

    AppUserDto findUserById(UUID tenantId, UUID id);

    String getUserAvatarById(UUID id);

    UserProfileResponse getUserProfile(UUID id);

    String handleActiveUser(UUID id, Boolean isActive, AppUserDto currentUser);

    AppUserDto findByEmail(String email);

    String syncUsers();

}
