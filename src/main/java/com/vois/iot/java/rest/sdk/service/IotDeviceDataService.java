package com.vois.iot.java.rest.sdk.service;

import com.vois.iot.java.rest.sdk.enums.ProductType;
import com.vois.iot.java.rest.sdk.model.CommonApiResponse;
import com.vois.iot.java.rest.sdk.model.DeviceLatLongData;
import com.vois.iot.java.rest.sdk.model.IotDeviceData;
import com.vois.iot.java.rest.sdk.model.IotDeviceDataResponse;
import com.vois.iot.java.rest.sdk.util.CsvUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.vois.iot.java.rest.sdk.constants.IotDeviceApiGlobalConstants.IOT_DEVICE_STATUS_ACTIVE;
import static com.vois.iot.java.rest.sdk.constants.IotDeviceApiGlobalConstants.IOT_DEVICE_STATUS_IN_ACTIVE;

/**
 * IotDeviceDataService
 * Service Logic for all the IOT Device Data APIs
 */
@Slf4j
@Service
public class IotDeviceDataService {

    private final CsvUtil csvUtil;
    List<IotDeviceData> events;

    public IotDeviceDataService(CsvUtil csvUtil) {
        this.csvUtil = csvUtil;
    }

    /**
     * @param filePath The path to the file directory in system.
     * @return Response id with Proper Response Message after Loading Csv Data
     */
    public CommonApiResponse loadCsvData(String filePath) {
        log.info("IotDeviceDataService :: loadCsvData method ");
        CommonApiResponse commonApiResponse = new CommonApiResponse();
        String responseCode;
        String responseMessage;

        try {
            //Parse Csv File
            events = csvUtil.parseCsv(filePath);
            responseCode = "CODE-VOIS-JAVA-SDK-000";
            responseMessage = "data refreshed";

        } catch (IOException e) {
            responseCode = "ERR-VOIS-JAVA-SDK-404";
            responseMessage = "ERROR: no data file found";
        } catch (Exception e) {
            responseCode = "ERR-VOIS-JAVA-SDK-500";
            responseMessage = "ERROR: A technical exception occurred.";
        }

        commonApiResponse.setResponseCode(responseCode);
        commonApiResponse.setResponseMessage(responseMessage);
        return commonApiResponse;
    }

    /**
     * Check if Data Loaded or not
     *
     * @return
     */
    public boolean checkDeviceDataAvailability() {
        log.info("IotDeviceDataService :: checkDeviceDataAvailability method ");

        //Return True if and only if Data is loaded and not empty.
        return events != null && !events.isEmpty();
    }

    /**
     * @param productId Input Product id.
     * @return List of Iot Device Data based on Product id.
     */
    public List<IotDeviceData> getDeviceDataByProductId(String productId) {
        log.info("IotDeviceDataService :: getDeviceDataByProductId method ");
        if (events == null || events.isEmpty()) {
            return List.of();
        }
        return events.stream()
                .filter(event -> event.getProductId().equals(productId))
                .toList();
    }

    /**
     * @param timestamp Input
     * @return IOT Device Data closest to timestamp.
     */
    public IotDeviceData findClosestDeviceDataBasedOnTimestamp(List<IotDeviceData> deviceDataList,
                                                               Long timestamp) {
        log.info("IotDeviceDataService :: findClosestDeviceDataBasedOnTimestamp method ");

        //if timestamp is null fetch data closest to currentTime
        long targetTimestamp = (timestamp != null) ? timestamp : System.currentTimeMillis();

        IotDeviceData iotDeviceData = deviceDataList.stream()
                .filter(event -> event.getDateTime() <= targetTimestamp)
                .max(Comparator.comparingLong(IotDeviceData::getDateTime))
                .orElse(null);

        // For Dynamic Activity Tracking
        if (iotDeviceData != null
                && iotDeviceData.getProductId().startsWith(ProductType.CYCLE_PLUS_TRACKER.getPrefix())) {
            //For Dynamic device Tracking
            String status = determineCyclePlusTrackerStatus(deviceDataList);
            log.info("Dynamic Activity Tracking Status For Device : {} with Product Id : {} ",
                    status, iotDeviceData.getProductId());
            iotDeviceData.setStatus(status);
        }

        return iotDeviceData;
    }

    /**
     * Determine CyclePlusTrackerStatus if Three Consecutive data having same GPS Location
     *
     * @param filteredDeviceDataList Input
     * @return status
     */
    private String determineCyclePlusTrackerStatus(List<IotDeviceData> filteredDeviceDataList) {
        log.info("IotDeviceDataService :: determineCyclePlusTrackerStatus method ");

        if (filteredDeviceDataList.size() < 3) {
            return "N/A";
        }
        boolean allConsecutiveRecords = filteredDeviceDataList.stream()
                .sorted(Comparator.comparing(IotDeviceData::getDateTime, Comparator.reverseOrder()))
                .limit(3)
                .map(data -> new DeviceLatLongData(data.getLatitude(), data.getLongitude()))
                .collect(Collectors.toSet())
                .size() == 1;

        return allConsecutiveRecords ? IOT_DEVICE_STATUS_IN_ACTIVE : IOT_DEVICE_STATUS_ACTIVE;
    }

    /**
     * @param iotDeviceData IOT Device Data Object (received from CSV loaded Data)
     * @return Custom IOT device Data response
     */
    public IotDeviceDataResponse prepareIotDeviceDataResponse(IotDeviceData iotDeviceData) {
        log.info("IotDeviceDataService :: prepareIotDeviceDataResponse method ");

        IotDeviceDataResponse iotDeviceDataResponse = new IotDeviceDataResponse();

        //Id
        iotDeviceDataResponse.setId(iotDeviceData.getProductId());
        //Name
        iotDeviceDataResponse.setName(ProductType.getProductName(iotDeviceData.getProductId()));

        //dateTime
        String formattedDateTime = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(iotDeviceData.getDateTime()), ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        iotDeviceDataResponse.setDatetime(formattedDateTime);

        //longitude
        iotDeviceDataResponse.setLongitude(String.valueOf(iotDeviceData.getLongitude()));
        //Latitude
        iotDeviceDataResponse.setLatitude(String.valueOf(iotDeviceData.getLatitude()));

        //Status
        if ((iotDeviceData.getLatitude() == 0 && iotDeviceData.getLongitude() == 0)
                || IOT_DEVICE_STATUS_IN_ACTIVE.equals(iotDeviceData.getStatus())) {
            iotDeviceDataResponse.setStatus(IOT_DEVICE_STATUS_IN_ACTIVE);
        } else {
            iotDeviceDataResponse.setStatus(IOT_DEVICE_STATUS_ACTIVE);
        }

        //Battery
        String batteryStatus = getBatteryStatus(iotDeviceData.getBattery());
        iotDeviceDataResponse.setBattery(batteryStatus);

        //Description
        if (iotDeviceData.getAirplaneMode().equals("ON")) {
            iotDeviceDataResponse.setDescription("SUCCESS: Location not available: Please turn off airplane mode.");
            iotDeviceDataResponse.setLongitude("");
            iotDeviceDataResponse.setLatitude("");
        } else {
            iotDeviceDataResponse.setDescription("SUCCESS: Location identified.");
        }
        return iotDeviceDataResponse;
    }

    private String getBatteryStatus(double battery) {
        if (battery >= 0.98) return "Full";
        if (battery >= 0.60) return "High";
        if (battery >= 0.40) return "Medium";
        if (battery >= 0.10) return "Low";
        return "Critical";
    }

}
