package com.example.callbotsms.service;


import com.example.callbotsms.dto.model.AppUserDto;
import com.example.callbotsms.dto.model.LogDto;
import com.example.callbotsms.dto.response.page.PageData;
import com.example.callbotsms.dto.response.page.PageLink;
import com.example.callbotsms.model.enums.ActionStatus;
import com.example.callbotsms.model.enums.ActionType;
import com.example.callbotsms.model.enums.EntityType;

import java.util.UUID;

public interface LogService {
    LogDto save(LogDto logDto, AppUserDto currentUser);

    LogDto save(LogDto logDto, UUID currentUserId, UUID tenantId);

    PageData<LogDto> findLogs(
            PageLink pageLink,
            EntityType entityType,
            UUID entityId,
            UUID userId,
            ActionStatus actionStatus,
            ActionType actionType,
            Long createdAtStartTs,
            Long createdAtEndTs,
            UUID tenantId,
            Boolean isSearchMatchCase
    );
}
