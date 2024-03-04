package com.example.callbotsms.dto.mapper;

import com.im.sso.dto.model.AppUserDto;
import com.im.sso.dto.response.UserProfileResponse;
import com.im.sso.model.AppUser;
import com.im.sso.model.Contact;
import com.im.sso.service.ContactService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(uses = {ContactService.class})
public interface AppUserMapper {

    AppUser toModel(AppUserDto userDto);

    @Mapping(source = "id", target = "avatar", qualifiedByName = "getAvatarUrl")
    UserProfileResponse toUserProfile(AppUser user);

    @Mapping(source = "id", target = "avatar", qualifiedByName = "getAvatarUrl")
    @Mapping(target = "contactId", source = "user.contact", qualifiedByName = "contactToUUID")
    AppUserDto toDto(AppUser user);

    @Named("contactToUUID")
    default UUID contactToUUID(Contact contact) {
        return contact != null ? contact.getId() : null;
    }

    @Named("getAvatarUrl")
    public static String getAvatarUrl(UUID id) {
        return "https://erp.innovation.com.vn/api/noauth/user/" + id + "/avatar";
    }

}
