package com.example.callbotsms.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserComponentDto {
    private UUID userId;
    private String componentName;
    private List<String> permissions;
}
