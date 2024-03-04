package com.example.callbotsms.dto.response;

import com.im.sso.dto.model.AppComponentDto;
import com.im.sso.dto.model.ContactDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String authority;
    private String role;
    private UUID tenantId;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;
    private ContactDto contact;
    private Collection<AppComponentDto> components;
    private boolean isEnabled;
    private String planName;
    private Long planExpiredIn;
}
