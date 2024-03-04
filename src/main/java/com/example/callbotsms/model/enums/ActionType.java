package com.example.callbotsms.model.enums;

import java.util.Arrays;

public enum ActionType {
    CREATED,
    UPDATED,
    ATTRIBUTES_UPDATED,
    CREDENTIALS_UPDATED,
    LOGIN,
    LOGOUT,
    ASSIGNED_TO_USER,
    UNASSIGNED_FROM_USER;

    public static ActionType lookup(final String id) {
        for (ActionType enumValues : ActionType.values()) {
            if (enumValues.name().equalsIgnoreCase(id)) {
                return enumValues;
            }
        }
        throw new RuntimeException(String.format("Invalid value for action type [%s]. " +
                "It should be %s", id, Arrays.asList(ActionType.values())));
    }
}
