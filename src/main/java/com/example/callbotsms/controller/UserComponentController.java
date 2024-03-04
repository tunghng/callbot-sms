package com.example.callbotsms.controller;

import com.im.sso.dto.model.AppComponentDto;
import com.im.sso.dto.model.AppUserDto;
import com.im.sso.dto.model.UserComponentDto;
import com.im.sso.dto.response.Response;
import com.im.sso.service.UserComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("api/user/{userId}/component/{component}")
public class UserComponentController extends BaseController {

    @Autowired
    UserComponentService userComponentService;

    @PostMapping
    @Operation(summary = "Assign Component with Permissions to User (assignComponentToUser)")
    public UserComponentDto assignComponentToUser(
            @Parameter(description = "Give userId to assign")
            @PathVariable("userId") UUID userId,
            @Parameter(description = "Give componentName to assign")
            @PathVariable("component") String componentName,
            @Parameter(description = "Give list permissions")
            @QueryParam("permissions") String permissions
    ) {
        AppUserDto currentUser = getCurrentUser();
        AppUserDto userDto = checkUserId(currentUser.getTenantId(), userId);
        AppComponentDto componentDto = checkComponentName(componentName);
        List<String> permissionList = Arrays.asList(permissions.split(","));
        validatePermissions(permissionList);
        return userComponentService.save(userDto, componentDto, permissionList, currentUser);
    }

    @DeleteMapping
    @Operation(summary = "Unassigned Component from User (unassignedComponentFromUser)")
    public Response unassignedComponentFromUser(
            @Parameter(description = "Give userId to un-assign")
            @PathVariable("userId") UUID userId,
            @Parameter(description = "Give componentName to un-assign")
            @PathVariable("component") String componentName
    ) {
        AppUserDto currentUser = getCurrentUser();
        AppUserDto userDto = checkUserId(currentUser.getTenantId(), userId);
        AppComponentDto componentDto = checkComponentName(componentName);
        return new Response(userComponentService.delete(userDto, componentDto, currentUser));
    }

}
