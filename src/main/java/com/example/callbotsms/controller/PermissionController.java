package com.example.callbotsms.controller;

import com.im.sso.dto.response.Response;
import com.im.sso.service.UserPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final UserPermissionService userPermissionService;

    @GetMapping("validate")
    @Operation(hidden = true)
    public ResponseEntity<?> validatePermission(
            @Parameter(description = "Give userId to validate")
            @RequestParam(required = false) UUID userId,
            @Parameter(description = "Give url to validate")
            @RequestParam(required = false) String url,
            @Parameter(description = "Give HTTP Method to validate")
            @RequestParam(required = false) String method
    ) {
        HttpStatus status = userPermissionService.validateUserPermission(userId, url, method) ?
                HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        Response response = status.is4xxClientError() ? new Response(status.value(), "You do not have permission to do this action.")
                : new Response(status.value(), "OK");
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("plan")
    @Operation(hidden = true)
    public ResponseEntity<?> validateSubscriptionPlan(
            @Parameter(description = "Give tenantId to validate")
            @RequestParam(required = false) UUID tenantId
    ) {
        HttpStatus status = userPermissionService.checkUserPlan(tenantId) ?
                HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        Response response = status.is4xxClientError() ? new Response(status.value(), "You need to upgrade your plan")
                : new Response(status.value(), "OK");
        return ResponseEntity.status(status).body(response);
    }
}
