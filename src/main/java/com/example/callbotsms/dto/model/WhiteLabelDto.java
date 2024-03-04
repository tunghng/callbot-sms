package com.example.callbotsms.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhiteLabelDto {

    @JsonIgnore
    private UUID id;

    private String logoImage;

    private String appTitle;

    @JsonIgnore
    private Date createdAt;

    @JsonIgnore
    private Date updatedAt;

}
