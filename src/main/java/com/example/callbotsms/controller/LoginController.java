package com.example.callbotsms.controller;

import com.example.callbotsms.dto.model.AppUserDto;
import com.example.callbotsms.dto.model.LogDto;
import com.example.callbotsms.dto.request.LoginRequest;
import com.example.callbotsms.dto.request.RefreshTokenRequest;
import com.example.callbotsms.dto.response.LoginResponse;
import com.example.callbotsms.exception.InvalidUsernameOrPassword;
import com.example.callbotsms.model.AppUser;
import com.example.callbotsms.model.enums.ActionStatus;
import com.example.callbotsms.model.enums.ActionType;
import com.example.callbotsms.model.enums.EntityType;
import com.example.callbotsms.security.exception.TokenRefreshException;
import com.example.callbotsms.security.model.SecurityUser;
import com.example.callbotsms.security.model.token.JwtTokenFactory;
import com.example.callbotsms.security.service.RefreshTokenService;
import com.example.callbotsms.security.service.SecurityUserService;
import com.example.callbotsms.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/auth")
public class LoginController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    SecurityUserService securityUserService;

    @Autowired
    JwtTokenFactory jwtTokenFactory;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    LogService logService;

    @Value(value = "${jwt.exp}")
    private Long jwtExp;

    @Value(value = "${jwt.refreshExp}")
    private Long jwtRefreshExp;

    @PostMapping("login")
    @Operation(summary = "Login method to get user JWT token data (loginEndpoint)")
    public ResponseEntity<LoginResponse> loginEndpoint(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

            String token = jwtTokenFactory.generateToken(securityUser, jwtExp);
            String refreshToken = jwtTokenFactory.generateToken(securityUser, jwtRefreshExp);

            refreshTokenService.createRefreshToken(securityUser.getUser(), refreshToken);
            ObjectMapper objectMapper = new ObjectMapper();
            logService.save(LogDto.builder()
                    .entityType(EntityType.USER)
                    .entityId(securityUser.getUser().getId())
                    .actionData(objectMapper.valueToTree(loginRequest))
                    .actionStatus(ActionStatus.SUCCESS)
                    .actionType(ActionType.LOGIN)
                    .build(), securityUser.getUser().getId(), securityUser.getUser().getTenantId());
            return ResponseEntity.ok(
                    new LoginResponse(token, refreshToken, jwtExp)
            );
        } catch (AuthenticationException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            logService.save(LogDto.builder()
                    .entityType(EntityType.USER)
                    .actionData(objectMapper.valueToTree(loginRequest))
                    .actionStatus(ActionStatus.FAILURE)
                    .actionType(ActionType.LOGIN)
                    .actionFailureDetails("Incorrect email or password").build(), new AppUserDto());
            throw new InvalidUsernameOrPassword("Incorrect email or password");
        }
    }

    @PostMapping("logout")
    @Operation(summary = "Logout (logout)")
    public ResponseEntity<Response> logout(HttpServletRequest request) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principle.toString().equals("anonymousUser")) {
            AppUser user = ((SecurityUser) principle).getUser();
            UUID userId = user.getId();
            refreshTokenService.deleteByUserId(userId);
            ObjectMapper objectMapper = new ObjectMapper();
            logService.save(LogDto.builder()
                    .entityType(EntityType.USER)
                    .entityId(user.getId())
                    .actionData(objectMapper.valueToTree(null))
                    .actionType(ActionType.LOGOUT)
                    .actionStatus(ActionStatus.SUCCESS)
                    .build(), user.getId(), user.getTenantId());
        }
        return ResponseEntity.ok(new Response("You've been signed out!"));
    }

    @PostMapping("token")
    @Operation(summary = "Refresh Token (refreshToken)")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    SecurityUser securityUser = securityUserService.loadUserByUsername(user.getEmail());
                    String token = jwtTokenFactory.generateToken(securityUser, jwtExp);
                    String refreshToken = jwtTokenFactory.generateToken(securityUser, jwtRefreshExp);
                    refreshTokenService.createRefreshToken(securityUser.getUser(), refreshToken);
                    ObjectMapper objectMapper = new ObjectMapper();
                    logService.save(LogDto.builder()
                                    .entityType(EntityType.USER)
                                    .entityId(securityUser.getUser().getId())
                                    .actionStatus(ActionStatus.SUCCESS)
                                    .actionData(objectMapper.valueToTree(request))
                                    .actionType(ActionType.LOGIN).build(),
                            securityUser.getUser().getId(), securityUser.getUser().getTenantId());
                    return ResponseEntity.ok(new LoginResponse(token, refreshToken, jwtExp));
                })
                .orElseThrow(() -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    logService.save(LogDto.builder()
                            .entityType(EntityType.USER)
                            .actionData(objectMapper.valueToTree(request))
                            .actionStatus(ActionStatus.FAILURE)
                            .actionType(ActionType.LOGIN)
                            .actionFailureDetails("Refresh token is not found!").build(), new AppUserDto());
                    return new TokenRefreshException(
                            request.getRefreshToken(),
                            "Refresh token is not found!"
                    );
                });
    }
}
