package com.example.callbotsms.model.enums;

import java.util.Arrays;

public enum AuthorityType {
    SYS_ADMIN,
    TENANT_ADMIN,
    CUSTOMER_USER;

    public static AuthorityType lookup(final String id) {
        for (AuthorityType enumValue : values()) {
            if (enumValue.name().equalsIgnoreCase(id)) {
                return enumValue;
            }
        }
        throw new RuntimeException(String.format("Invalid value for authority type [%s]. " +
                "It should be %s", id, Arrays.asList(AuthorityType.values())));
    }
}
