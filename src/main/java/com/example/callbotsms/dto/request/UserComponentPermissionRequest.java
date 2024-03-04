package com.example.callbotsms.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserComponentPermissionRequest extends UserComponentRequest {
    private List<String> permissions;

    public UserComponentPermissionRequest(UUID userId, String componentName, List<String> permissions) {
        super(userId, componentName);
        this.permissions = permissions;
    }
}
