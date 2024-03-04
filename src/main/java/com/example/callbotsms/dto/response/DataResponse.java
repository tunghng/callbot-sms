package com.example.callbotsms.dto.response;

import com.im.sso.dto.model.DataKvDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DataResponse {
    private List<DataKvDto> data;
}
