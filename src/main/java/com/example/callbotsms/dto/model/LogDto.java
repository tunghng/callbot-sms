package com.example.callbotsms.dto.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.im.sso.model.enums.ActionStatus;
import com.im.sso.model.enums.ActionType;
import com.im.sso.model.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogDto {
    private UUID id;
    private UUID tenantId;
    private UUID entityId;
    private EntityType entityType;
    private AppUserDto createdBy;
    private JsonNode actionData;
    private ActionStatus actionStatus;
    private ActionType actionType;
    private String actionFailureDetails;
    private Date createdAt;
}
