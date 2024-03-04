package com.example.callbotsms.controller;

import com.im.sso.dto.response.DataResponse;
import com.im.sso.service.AppInfoService;
import com.im.sso.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/noauth")
public class NoAuthController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    AppInfoService appInfoService;

    @GetMapping("info")
    @Operation(summary = "Get Application Information")
    public DataResponse getAppInfo() {
        return new DataResponse(appInfoService.findInfo());
    }

//    @GetMapping("user/{userId}/avatar")
//    @Operation(summary = "Get Avatar User by id (getAvatarUserById)")
//    public ResponseEntity<String> getAvatarUserById(@PathVariable UUID userId) {
//        return ResponseEntity.ok(
//                userService.getUserAvatarById(userId)
//        );
//    }

    @GetMapping("user/{userId}/avatar")
    @Operation(summary = "Get Avatar User by id (getAvatarUserById)")
    public ResponseEntity<byte[]> getAvatarUserById(@PathVariable UUID userId) {
        String data = userService.getUserAvatarById(userId);
        if (data == null || data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String[] parts = data.split(",");
        if (parts.length != 2) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String base64Image = parts[1];
        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

    }
}
