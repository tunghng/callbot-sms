package com.example.callbotsms.service;

import com.im.sso.dto.mapper.AppUserMapper;
import com.im.sso.dto.model.AppUserDto;
import com.im.sso.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserMapperServiceImpl implements UserMapperService {

    private final AppUserRepository userRepository;

    private final AppUserMapper userMapper;

    @Override
    public AppUserDto findById(UUID id) {
        if (id == null) return null;
        return userMapper.toDto(userRepository.findById(id).orElse(null));
    }
}
