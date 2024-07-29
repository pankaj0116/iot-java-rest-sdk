package com.vois.iot.java.rest.sdk.controller;

import com.vois.iot.java.rest.sdk.model.CommonApiResponse;
import com.vois.iot.java.rest.sdk.model.IotDeviceData;
import com.vois.iot.java.rest.sdk.model.IotDeviceDataResponse;
import com.vois.iot.java.rest.sdk.model.LoadDataApiRequest;
import com.vois.iot.java.rest.sdk.model.LoadDataApiResponse;
import com.vois.iot.java.rest.sdk.service.IotDeviceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * IotDeviceDataController Class
 * Rest Controller for Handling IOT Device Data APIs
 */
@RestController
@RequestMapping("/iot/event/v1")
@Slf4j
public class IotDeviceDataController {

    private final IotDeviceDataService iotDeviceDataService;

    public IotDeviceDataController(IotDeviceDataService iotDeviceDataService) {
        this.iotDeviceDataService = iotDeviceDataService;
    }

    @PostMapping
    public ResponseEntity<LoadDataApiResponse> loadCsvData(@RequestBody LoadDataApiRequest loadDataApiRequest) {
        log.info("IotDeviceDataController :: loadCsvData method ");

        LoadDataApiResponse loadDataApiResponse = new LoadDataApiResponse();
        String filePath = loadDataApiRequest.getFilepath();

        //Loading CSV file.
        CommonApiResponse response = iotDeviceDataService.loadCsvData(filePath);
        loadDataApiResponse.setDescription(response.getResponseMessage());

        if (response.getResponseCode().equals("CODE-VOIS-JAVA-SDK-000")) {
            return ResponseEntity.ok(loadDataApiResponse);
        } else if (response.getResponseCode().equals("ERR-VOIS-JAVA-SDK-404")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(loadDataApiResponse);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loadDataApiResponse);
        }

    }

    @GetMapping
        public ResponseEntity<Object> getDeviceByProductIdAndTimestamp(@RequestParam(value = "ProductId", required = true)
                                                                   String productId,
                                                                   @RequestParam(value = "tstmp", required = false)
                                                                   Long timestamp) {
        log.info("IotDeviceDataController :: getDeviceDetails method ");

        if (productId == null || productId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoadDataApiResponse("ERROR: productId is required."));
        }

        //Check if device data is loaded or not
        boolean check = iotDeviceDataService.checkDeviceDataAvailability();
        if (!check) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new LoadDataApiResponse("ERROR: CSV Data not loaded. Please load CSV data."));
        }

        //Fetch IOT Device Data List By Product id.
        List<IotDeviceData> deviceDataList = iotDeviceDataService.getDeviceDataByProductId(productId);

        //Check if device not found in loaded data
        if (deviceDataList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new LoadDataApiResponse("ERROR: Id " + productId + " not found"));
        }

        //find Iot Device Data closest to timestamp
        IotDeviceData iotDeviceData = iotDeviceDataService
                .findClosestDeviceDataBasedOnTimestamp(deviceDataList, timestamp);

        if (iotDeviceData == null || (iotDeviceData.getAirplaneMode().equals("OFF") &&
                (iotDeviceData.getLatitude() == 0 && iotDeviceData.getLongitude() == 0))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoadDataApiResponse("ERROR: Device could not be located"));
        }

        //Prepare a custom Response for IOT Device Data
        IotDeviceDataResponse iotDeviceDataResponse = iotDeviceDataService.prepareIotDeviceDataResponse(iotDeviceData);

        return ResponseEntity.ok(iotDeviceDataResponse);

    }

}
