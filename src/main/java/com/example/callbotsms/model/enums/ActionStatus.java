package com.example.callbotsms.model.enums;

import java.util.Arrays;

public enum ActionStatus {
    SUCCESS,
    FAILURE;

    public static ActionStatus lookup(final String id) {
        for (ActionStatus enumValues : ActionStatus.values()) {
            if (enumValues.name().equalsIgnoreCase(id)) {
                return enumValues;
            }
        }
        throw new RuntimeException(String.format("Invalid value for action status [%s]. " +
                "It should be %s", id, Arrays.asList(ActionStatus.values())));
    }
}
