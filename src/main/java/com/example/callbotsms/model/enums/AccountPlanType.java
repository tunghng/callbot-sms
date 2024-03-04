package com.example.callbotsms.model.enums;

import java.util.Arrays;

public enum AccountPlanType {
    BASIC, DIGITAL, OPERATION;

    public static AccountPlanType lookup(final String id) {
        for (AccountPlanType enumValue : values()) {
            if (enumValue.name().equalsIgnoreCase(id)) {
                return enumValue;
            }
        }
        throw new RuntimeException(String.format("Invalid value for account plan [%s]. " +
                "It should be %s", id, Arrays.asList(AccountPlanType.values())));
    }
}
