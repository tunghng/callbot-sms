package com.example.callbotsms.model.enums;

import java.util.Arrays;

public enum PermissionType {
    READ, ADD, EDIT;

    public static PermissionType lookup(final String id) {
        for (PermissionType enumValue : values()) {
            if (enumValue.name().equalsIgnoreCase(id)) {
                return enumValue;
            }
        }
        throw new RuntimeException(String.format("Invalid value for permission type [%s]. " +
                "It should be %s", id, Arrays.asList(AuthorityType.values())));
    }
}
