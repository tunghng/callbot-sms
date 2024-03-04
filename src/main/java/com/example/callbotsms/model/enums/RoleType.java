package com.example.callbotsms.model.enums;

import java.util.Arrays;

public enum RoleType {
    SYS_ADMIN,
    TENANT,
    MANAGER,
    CUSTOMER;

    public static RoleType lookup(final String id) {
        for (RoleType enumValue : values()) {
            if (enumValue.name().equalsIgnoreCase(id)) {
                return enumValue;
            }
        }
        throw new RuntimeException(String.format("Invalid value for role type [%s]. " +
                "It should be %s", id, Arrays.asList(RoleType.values())));
    }
}
