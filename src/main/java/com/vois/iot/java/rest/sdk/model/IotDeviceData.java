package com.vois.iot.java.rest.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceData {

    private long dateTime;
    private int eventId;
    private String productId;
    private double latitude;
    private double longitude;
    private double battery;
    private String light;
    private String airplaneMode;

    //for dynamic Activity Training
    private String status;

}
