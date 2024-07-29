package com.vois.iot.java.rest.sdk.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommonApiResponse {

    private String responseCode;
    private String responseMessage;

    public CommonApiResponse(String responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }
}
