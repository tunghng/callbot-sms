package com.example.callbotsms.service;

import com.im.sso.dto.model.AppUserDto;

import java.util.UUID;

public interface UserMapperService {
    AppUserDto findById(UUID id);

}
