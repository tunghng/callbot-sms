package com.example.callbotsms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.sso.dto.model.AppUserDto;
import com.im.sso.dto.model.LogDto;
import com.im.sso.dto.request.UserPlanRequest;
import com.im.sso.dto.response.Response;
import com.im.sso.dto.response.page.PageData;
import com.im.sso.dto.response.page.PageLink;
import com.im.sso.exception.ForbiddenException;
import com.im.sso.model.enums.ActionStatus;
import com.im.sso.model.enums.AuthorityType;
import com.im.sso.model.enums.EntityType;
import com.im.sso.service.AccountPlanService;
import com.im.sso.service.LogService;
import com.im.sso.service.UserPlanService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user/plan")
public class UserPlanController extends BaseController {

    private final AccountPlanService accountPlanService;

    private final UserPlanService userPlanService;

    private final LogService logService;

    @GetMapping("list")
    ResponseEntity<PageData<?>> getAccountPlans(
            @Parameter(description = "Sequence number of page starting from 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Maximum amount of entities in a one page")
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        PageLink pageLink = createPageLink(page, pageSize, null, null, null);
        return ResponseEntity.ok(
                accountPlanService.findAccountPlans(pageLink)
        );
    }

    @PostMapping
    ResponseEntity<Response> saveUserPlan(
            @RequestBody UserPlanRequest userPlanRequest
    ) {
        AppUserDto currentUser = getCurrentUser();
        if (!currentUser.getAuthority().equals(AuthorityType.SYS_ADMIN.name())) {
            ObjectMapper objectMapper = new ObjectMapper();
            logService.save(LogDto.builder()
                    .entityType(EntityType.USER_PLAN)
                    .actionData(objectMapper.valueToTree(userPlanRequest))
                    .actionStatus(ActionStatus.FAILURE)
                    .actionFailureDetails("You do not have permission to do this action").build(), currentUser);
            throw new ForbiddenException("You do not have permission to do this action");
        }
        return ResponseEntity.ok(
                new Response(userPlanService.save(userPlanRequest, currentUser))
        );
    }
}
