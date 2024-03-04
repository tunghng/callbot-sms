package com.example.callbotsms.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class Response {
    private final int status;
    private final Date timestamp;
    private String message;

    public Response(String message) {
        this.status = 200;
        this.message = message;
        this.timestamp = new Date();
    }

    public Response(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
