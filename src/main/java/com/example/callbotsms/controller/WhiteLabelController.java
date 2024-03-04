package com.example.callbotsms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.sso.dto.model.AppUserDto;
import com.im.sso.dto.model.LogDto;
import com.im.sso.dto.model.WhiteLabelDto;
import com.im.sso.exception.ForbiddenException;
import com.im.sso.model.enums.ActionStatus;
import com.im.sso.model.enums.ActionType;
import com.im.sso.model.enums.AuthorityType;
import com.im.sso.model.enums.EntityType;
import com.im.sso.service.LogService;
import com.im.sso.service.WhiteLabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api")
public class WhiteLabelController extends BaseController {

    @Autowired
    WhiteLabelService whiteLabelService;

    @Autowired
    LogService logService;

    @GetMapping("whiteLabel")
    @Operation(summary = "Get Current Tenant White Label (getTenantWhiteLabel)")
    public WhiteLabelDto getTenantWhiteLabel() {
        AppUserDto currentUser = getCurrentUser();
        return whiteLabelService.findByTenantId(currentUser.getTenantId());
    }

    @PostMapping("admin/whiteLabel")
    @Operation(
            summary = "Save Current Tenant White Label (saveTenantWhiteLabel)",
            description = "Available for SYS_ADMIN, TENANT_ADMIN"
    )
    public WhiteLabelDto saveTenantWhiteLabel(
            @Valid @RequestBody WhiteLabelDto whiteLabelDto
    ) {
        AppUserDto currentUser = getCurrentUser();
        if (currentUser.getAuthority().equals(AuthorityType.CUSTOMER_USER.name())) {
            ObjectMapper objectMapper = new ObjectMapper();
            logService.save(LogDto.builder()
                    .entityType(EntityType.WHITE_LABEL)
                    .actionType(whiteLabelDto.getId() != null ? ActionType.UPDATED : ActionType.CREATED)
                    .actionStatus(ActionStatus.FAILURE)
                    .actionData(objectMapper.valueToTree(whiteLabelDto))
                    .actionFailureDetails("You do not have permission to do this action")
                    .build(), currentUser);
            throw new ForbiddenException("You do not have permission to do this action");
        }
        return whiteLabelService.save(whiteLabelDto, currentUser);
    }
}
