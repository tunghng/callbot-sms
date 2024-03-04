package com.example.callbotsms.controller;

import com.example.callbotsms.dto.model.AppUserDto;
import com.example.callbotsms.dto.request.ChangePasswordRequest;
import com.example.callbotsms.dto.request.SignUpRequest;
import com.example.callbotsms.dto.response.Response;
import com.example.callbotsms.dto.response.UserProfileResponse;
import com.example.callbotsms.service.LogService;
import com.example.callbotsms.service.UserCredentialsService;
import com.example.callbotsms.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("api/auth")
public class AuthController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    UserCredentialsService userCredentialsService;

    @Autowired
    LogService logService;

    @GetMapping("user")
    @Operation(summary = "Get current user (getCurrentUser)")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        return ResponseEntity.ok(
                userService.getUserProfile(getCurrentUser().getId())
        );
    }

    @PostMapping("user")
    @Operation(summary = "Save current user (saveCurrentUser)")
    public ResponseEntity<UserProfileResponse> saveCurrentUser(@Valid @RequestBody AppUserDto userDto) {
        AppUserDto currentUser = getCurrentUser();
        BeanUtils.copyProperties(currentUser, userDto,
                "firstName", "lastName",
                "phone", "avatar");
        userService.save(userDto, currentUser);
        return ResponseEntity.ok(
                userService.getUserProfile(currentUser.getId())
        );
    }

    @PostMapping("password/change")
    @Operation(summary = "Change password for current user (changePassword)")
    public ResponseEntity<Response> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        AppUserDto currentUser = getCurrentUser();
        userCredentialsService.changePassword(currentUser, changePasswordRequest);
        return ResponseEntity.ok(new Response("Password updated successfully"));
    }

    @PostMapping("signup")
    @Operation(summary = "Sign Up Customer (signUp)")
    public ResponseEntity<?> signUp(
            @RequestBody SignUpRequest signUpRequest
    ) {
        AppUserDto appUserDto = userService.signUp(signUpRequest);
        userCredentialsService.setPassword(appUserDto.getId(), signUpRequest.getPassword());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new Response(HttpStatus.OK.value(),
                        String.format("User with id [%s] sign up successful", appUserDto.getId())));
    }
}
