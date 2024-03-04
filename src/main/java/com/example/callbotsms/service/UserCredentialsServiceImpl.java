package com.example.callbotsms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.sso.dto.model.AppUserDto;
import com.im.sso.dto.model.LogDto;
import com.im.sso.dto.request.ChangePasswordRequest;
import com.im.sso.exception.InvalidOldPasswordException;
import com.im.sso.model.UserCredential;
import com.im.sso.model.enums.ActionStatus;
import com.im.sso.model.enums.ActionType;
import com.im.sso.model.enums.EntityType;
import com.im.sso.repository.UserCredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCredentialsServiceImpl implements UserCredentialsService {

    private final UserCredentialsRepository userCredentialsRepository;

    @Autowired
    LogService logService;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.password.default}")
    private String defaultPassword;

    @Override
    public boolean isEnabled(UUID userId) {
        return userCredentialsRepository.findByUserId(userId).orElse(new UserCredential()).isEnabled();
    }

    @Override
    public void changePassword(AppUserDto currentUser, ChangePasswordRequest passwordRequest) {
        isValidOldPassword(currentUser, passwordRequest);
        setPassword(currentUser.getId(), passwordRequest.getNewPassword());
        ObjectMapper objectMapper = new ObjectMapper();
        logService.save(LogDto.builder()
                .entityType(EntityType.USER_CREDENTIALS)
                .entityId(currentUser.getId())
                .actionData(objectMapper.valueToTree(passwordRequest))
                .actionStatus(ActionStatus.SUCCESS)
                .actionType(ActionType.CREDENTIALS_UPDATED)
                .build(), currentUser);
    }

    @Override
    public void setPassword(UUID userId, String password) {
        UserCredential userCredential = userCredentialsRepository.findById(userId).orElse(
                new UserCredential()
        );
        userCredential.setUserId(userId);
        userCredential.setPassword(passwordEncoder.encode(password));
        userCredentialsRepository.saveAndFlush(userCredential);
    }

    @Override
    public void setPassword(UUID userId) {
        setPassword(userId, defaultPassword);
    }

    private void isValidOldPassword(AppUserDto currentUser, ChangePasswordRequest passwordRequest) {
        UserCredential userCredential = userCredentialsRepository.findByUserId(currentUser.getId()).orElse(new UserCredential());
        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), userCredential.getPassword())) {
            ObjectMapper objectMapper = new ObjectMapper();
            logService.save(LogDto.builder()
                    .entityType(EntityType.USER_CREDENTIALS)
                    .entityId(userCredential.getUserId())
                    .actionData(objectMapper.valueToTree(passwordRequest))
                    .actionType(ActionType.CREDENTIALS_UPDATED)
                    .actionFailureDetails("Did not match current password")
                    .actionStatus(ActionStatus.FAILURE).build(), currentUser);
            throw new InvalidOldPasswordException("Did not match current password");
        }
    }
}
