package com.example.callbotsms.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppComponentDto {
    @JsonIgnore
    private UUID id;
    private String name;
    private String description;
    private String urlBase;
    private Collection<String> permissions;
}
