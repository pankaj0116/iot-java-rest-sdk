package com.vois.iot.java.rest.sdk.controller;

import com.vois.iot.java.rest.sdk.model.CommonApiResponse;
import com.vois.iot.java.rest.sdk.model.IotDeviceData;
import com.vois.iot.java.rest.sdk.model.IotDeviceDataResponse;
import com.vois.iot.java.rest.sdk.model.LoadDataApiRequest;
import com.vois.iot.java.rest.sdk.model.LoadDataApiResponse;
import com.vois.iot.java.rest.sdk.service.IotDeviceDataService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
class IotDeviceDataControllerTest {

    @InjectMocks
    private IotDeviceDataController iotDeviceDataController;

    @Mock
    private IotDeviceDataService iotDeviceDataService;

    @Test
    void testLoadCsvData_Success() {
        // Arrange
        LoadDataApiRequest request = new LoadDataApiRequest();
        request.setFilepath("path/to/file.csv");
        CommonApiResponse apiResponse = new CommonApiResponse();
        apiResponse.setResponseCode("CODE-VOIS-JAVA-SDK-000");
        apiResponse.setResponseMessage("File loaded successfully");
        when(iotDeviceDataService.loadCsvData(anyString())).thenReturn(apiResponse);

        // Act
        ResponseEntity<LoadDataApiResponse> response = iotDeviceDataController.loadCsvData(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("File loaded successfully", response.getBody().getDescription());
    }

    @Test
    void testLoadCsvData_FileNotFound() {
        // Arrange
        LoadDataApiRequest request = new LoadDataApiRequest();
        request.setFilepath("path/to/file.csv");
        CommonApiResponse apiResponse = new CommonApiResponse();
        apiResponse.setResponseCode("ERR-VOIS-JAVA-SDK-404");
        apiResponse.setResponseMessage("File not found");
        when(iotDeviceDataService.loadCsvData(anyString())).thenReturn(apiResponse);

        // Act
        ResponseEntity<LoadDataApiResponse> response = iotDeviceDataController.loadCsvData(request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("File not found", response.getBody().getDescription());
    }

    @Test
    void testGetDeviceByProductIdAndTimestamp_Success() {
        // Arrange
        String productId = "device123";
        Long timestamp = 123456789L;
        List<IotDeviceData> deviceDataList = Collections.singletonList(new IotDeviceData());
        IotDeviceData iotDeviceData = new IotDeviceData();
        iotDeviceData.setAirplaneMode("ON");
        iotDeviceData.setLatitude(10.0);
        iotDeviceData.setLongitude(20.0);
        when(iotDeviceDataService.checkDeviceDataAvailability()).thenReturn(true);
        when(iotDeviceDataService.getDeviceDataByProductId(anyString())).thenReturn(deviceDataList);
        when(iotDeviceDataService.findClosestDeviceDataBasedOnTimestamp(anyList(), anyLong())).thenReturn(iotDeviceData);
        when(iotDeviceDataService.prepareIotDeviceDataResponse(any(IotDeviceData.class))).thenReturn(new IotDeviceDataResponse());

        // Act
        ResponseEntity<Object> response = iotDeviceDataController.getDeviceByProductIdAndTimestamp(productId, timestamp);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetDeviceByProductIdAndTimestamp_NotFound() {
        // Arrange
        String productId = "device123";
        Long timestamp = 123456789L;
        when(iotDeviceDataService.checkDeviceDataAvailability()).thenReturn(true);
        when(iotDeviceDataService.getDeviceDataByProductId(anyString())).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<Object> response = iotDeviceDataController.getDeviceByProductIdAndTimestamp(productId, timestamp);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetDeviceByProductIdAndTimestamp_No_DeviceData_NotFound() {
        // Arrange
        String productId = "device123";
        Long timestamp = 123456789L;
        when(iotDeviceDataService.checkDeviceDataAvailability()).thenReturn(false);

        // Act
        ResponseEntity<Object> response = iotDeviceDataController.getDeviceByProductIdAndTimestamp(productId, timestamp);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetDeviceByProductIdAndTimestamp_BadRequest_NoProductId() {
        // Act
        ResponseEntity<Object> response = iotDeviceDataController.getDeviceByProductIdAndTimestamp(null, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ERROR: productId is required.", ((LoadDataApiResponse) response.getBody()).getDescription());
    }
}
