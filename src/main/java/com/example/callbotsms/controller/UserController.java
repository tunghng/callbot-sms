package com.example.callbotsms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.sso.dto.model.AppUserDto;
import com.im.sso.dto.model.LogDto;
import com.im.sso.dto.request.UserActivateRequest;
import com.im.sso.dto.response.Response;
import com.im.sso.dto.response.UserProfileResponse;
import com.im.sso.dto.response.page.PageData;
import com.im.sso.dto.response.page.PageLink;
import com.im.sso.exception.BadRequestException;
import com.im.sso.exception.ForbiddenException;
import com.im.sso.model.enums.*;
import com.im.sso.service.LogService;
import com.im.sso.service.UserCredentialsService;
import com.im.sso.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.PathParam;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("api/user")
public class UserController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    UserCredentialsService userCredentialsService;
    @Autowired
    LogService logService;

    @GetMapping
    @Operation(summary = "Get Users (getUsers)")
    public ResponseEntity<PageData<?>> getUsers(
            @Parameter(description = "Sequence number of page starting from 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Maximum amount of entities in a one page")
            @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Filter by role name")
            @RequestParam(required = false) RoleType role,
            @Parameter(description = "Filter by contactId")
            @RequestParam(required = false) UUID contactId,
            @Parameter(description = "Search columns: email, phone, firstName, lastName")
            @RequestParam(required = false) String searchText,
            @Parameter(description = "Property of entity to sort by")
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = "Sort order. ASC (ASCENDING) or DESC (DESCENDING)")
            @RequestParam(required = false) String sortOrder,
            @Parameter(description = "Filter column: createdAt. `createdAtEndTs` is required.")
            @RequestParam(required = false) Long createdAtStartTs,
            @Parameter(description = "Filter column: createdAt. `createdAtStartTs` is required.")
            @RequestParam(required = false) Long createdAtEndTs,
            @Parameter(description = "Filter column: isEnabled with true and false.")
            @RequestParam(required = false) Boolean isEnabled,
            @Parameter(description = "Search Match Case Or Not")
            @RequestParam(defaultValue = "false") Boolean isSearchMatchCase
    ) {
        PageLink pageLink = createPageLink(
                page, pageSize, searchText, sortProperty, sortOrder
        );
        return ResponseEntity.ok(
                userService.findUsers(
                        pageLink, role, contactId, getCurrentUser(), createdAtStartTs, createdAtEndTs, isEnabled, isSearchMatchCase
                )
        );
    }

    @GetMapping("{userId}")
    @Operation(summary = "Get User by id (getUserById)")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @GetMapping("isUserExist")
    @Operation(summary = "Check exist account by email (isEmailExist)")
    public ResponseEntity<Response> isEmailExist(@PathParam("email") String email) {
        AppUserDto userDto = userService.findByEmail(email);
        if (userDto != null) {
            throw new BadRequestException("User with email [" + email + "] is already exist");
        }
        return ResponseEntity.ok(
                new Response("User with email [" + email + "] is not exist")
        );
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Save User (saveUser)")
    public AppUserDto saveUser(@Valid @RequestBody AppUserDto userDto) {
        AppUserDto currentUser = getCurrentUser();
        return userService.save(userDto, currentUser);
    }

    @PutMapping("{userId}/activate")
    @Transactional
    @Operation(summary = "Activate user (activateUser)")
    public ResponseEntity<Response> activeUser(
            @PathVariable UUID userId
    ) {
        AppUserDto currentUser = getCurrentUser();
        AppUserDto user = checkUserId(currentUser.getTenantId(), userId);
        if (user.getAuthority().equals(AuthorityType.SYS_ADMIN.toString())
                || currentUser.getId().equals(userId)) {
            ObjectMapper objectMapper = new ObjectMapper();
            logService.save(LogDto.builder()
                    .entityType(EntityType.USER)
                    .entityId(userId)
                    .actionStatus(ActionStatus.FAILURE)
                    .actionType(ActionType.ATTRIBUTES_UPDATED)
                    .actionData(objectMapper.valueToTree(new UserActivateRequest(userId, true)))
                    .actionFailureDetails("SYS_ADMIN is not allowed to activate himself.")
                    .build(), currentUser);
            throw new ForbiddenException("SYS_ADMIN is not allowed to activate himself.");
        }
        return ResponseEntity.ok(
                new Response(userService.handleActiveUser(userId, true, currentUser))
        );
    }

    @DeleteMapping("{userId}/activate")
    @Transactional
    @Operation(summary = "Deactivate user (deactivateUser)")
    public ResponseEntity<Response> deactivateUser(
            @PathVariable UUID userId
    ) {
        AppUserDto currentUser = getCurrentUser();
        AppUserDto user = checkUserId(currentUser.getTenantId(), userId);
        if (user.getAuthority().equals(AuthorityType.SYS_ADMIN.toString())
                || currentUser.getId().equals(userId)) {
            throw new ForbiddenException("SYS_ADMIN is not allowed to deactivate himself.");
        }
        return ResponseEntity.ok(
                new Response(userService.handleActiveUser(userId, false, currentUser))
        );
    }

    @PostMapping("{userId}/resetPassword")
    @Transactional
    @Operation(summary = "Reset user password to default (resetUserPassword)")
    public ResponseEntity<Response> resetPassword(@PathVariable UUID userId) {
        AppUserDto currentUser = getCurrentUser();
        checkUserId(currentUser.getTenantId(), userId);
        userCredentialsService.setPassword(userId);
        return ResponseEntity.ok(
                new Response(String.format("User with id [%s] reset password successful", userId))
        );
    }

    @GetMapping("sync")
    @Operation(summary = "Sync User Database (syncUsers)", hidden = true)
    public ResponseEntity<Response> syncUsers() {
        return ResponseEntity.ok(new Response(userService.syncUsers()));
    }


}
