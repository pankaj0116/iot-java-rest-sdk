package com.vois.iot.java.rest.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * For Dynamic Activity Tracker
 * (checking for identical Lat-Long for consecutive device data)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceLatLongData {
    private double latitude;
    private double longitude;
}
