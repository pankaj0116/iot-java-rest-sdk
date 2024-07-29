package com.vois.iot.java.rest.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceDataResponse {
    private String id;
    private String name;
    private String datetime;
    @JsonProperty("long")
    private String longitude;
    @JsonProperty("lat")
    private String latitude;
    private String status;
    private String battery;
    private String description;
}
