package com.example.callbotsms.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UserPlanRequest {
    UUID userId;
    String planName;
    Long expiredIn;
}
