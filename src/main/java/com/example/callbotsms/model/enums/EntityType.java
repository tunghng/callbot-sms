package com.example.callbotsms.model.enums;

import java.util.Arrays;

public enum EntityType {
    ACCOUNT_PLAN,
    USER,
    USER_CREDENTIALS,
    USER_COMPONENT,
    USER_PLAN,
    DATA_KV,
    WHITE_LABEL;

    public static EntityType lookup(final String id) {
        for (EntityType enumValues : EntityType.values()) {
            if (enumValues.name().equalsIgnoreCase(id)) {
                return enumValues;
            }
        }
        throw new RuntimeException(String.format("Invalid value for entity type [%s]. " +
                "It should be %s", id, Arrays.asList(EntityType.values())));
    }
}
