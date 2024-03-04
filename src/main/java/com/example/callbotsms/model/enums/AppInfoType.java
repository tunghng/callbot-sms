package com.example.callbotsms.model.enums;

import java.util.Arrays;

public enum AppInfoType {
    APP_VERSION,
    APP_HOTLINE,
    HOSTNAME,
    SIGNUP_AVAILABLE,
    DEFAULT_TENANT_EMAIL,
    DEFAULT_CONTACT_ID;

    public static AppInfoType lookup(final String id) {
        for (AppInfoType enumValue : values()) {
            if (enumValue.name().equalsIgnoreCase(id)) {
                return enumValue;
            }
        }
        throw new RuntimeException(String.format("Invalid value for app info [%s]. " +
                "It should be %s", id, Arrays.asList(AppInfoType.values())));
    }
}
