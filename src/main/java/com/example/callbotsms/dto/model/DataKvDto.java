package com.example.callbotsms.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataKvDto implements Serializable {
    private String key;
    private String value;
    @JsonIgnore
    private Date createdAt;
    private Date updatedAt;
}
