package com.example.callbotsms.controller;



import com.example.callbotsms.dto.model.AppUserDto;
import com.example.callbotsms.dto.response.page.PageLink;
import com.example.callbotsms.dto.response.page.SortOrder;
import com.example.callbotsms.exception.*;
import com.example.callbotsms.model.enums.PermissionType;
import com.example.callbotsms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class BaseController {

    @Autowired
    UserService userService;

    public PageLink createPageLink(int page, int pageSize, String searchText,
                                   String sortProperty, String sortOrder) {
        if (!StringUtils.isEmpty(sortProperty)) {
            SortOrder.Direction direction = SortOrder.Direction.DESC;
            if (!StringUtils.isEmpty(sortOrder)) {
                direction = SortOrder.Direction.lookup(sortOrder.toUpperCase());
            }
            SortOrder sort = new SortOrder(sortProperty, direction);
            return new PageLink(page, pageSize, searchText, sort);
        } else {
            return new PageLink(page, pageSize, searchText);
        }
    }

    protected AppUserDto getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUserDto currentUser = userService.findByEmail(email);
        if (currentUser == null) {
            throw new BadRequestException("You aren't authorized to perform this operation.");
        }
        return currentUser;
    }

    AppUserDto checkUserId(UUID tenantId, UUID userId) {
        AppUserDto userDto = userService.findUserById(tenantId, userId);
        checkNotNull(userDto, String.format("User with id [%s] is not found", userId));
        return userDto;
    }



    void validatePermissions(List<String> list) {
        list.forEach(item -> PermissionType.lookup(item));
    }

    <T> T checkNotNull(T reference, String notFoundMessage) {
        if (reference == null) {
            throw new NotFoundException(notFoundMessage);
        }
        return reference;
    }
}
