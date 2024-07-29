package com.vois.iot.java.rest.sdk.service;

import com.vois.iot.java.rest.sdk.enums.ProductType;
import com.vois.iot.java.rest.sdk.model.CommonApiResponse;
import com.vois.iot.java.rest.sdk.model.IotDeviceData;
import com.vois.iot.java.rest.sdk.model.IotDeviceDataResponse;
import com.vois.iot.java.rest.sdk.util.CsvUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class IotDeviceDataServiceTest {

    @InjectMocks
    private IotDeviceDataService iotDeviceDataService;

    @Mock
    private CsvUtil csvUtil;

    @Test
    void testLoadCsvDataSuccess() throws IOException {
        String filePath = "path/to/file.csv";
        List<IotDeviceData> mockData = Arrays.asList(
                new IotDeviceData(1L, 1, "WG11155638", 1.0, 1.0,
                        0.9, "OFF", "OFF", "Active"),
                new IotDeviceData(1L, 1, "6900001001", 1.0, 1.0,
                        0.9, "OFF", "OFF", "Inactive")
        );
        when(csvUtil.parseCsv(filePath)).thenReturn(mockData);

        CommonApiResponse response = iotDeviceDataService.loadCsvData(filePath);

        assertEquals("CODE-VOIS-JAVA-SDK-000", response.getResponseCode());
        assertEquals("data refreshed", response.getResponseMessage());
        assertTrue(iotDeviceDataService.checkDeviceDataAvailability());
    }

    @Test
    void testLoadCsvDataFileNotFound() throws IOException {
        String filePath = "path/to/nonexistent.csv";
        when(csvUtil.parseCsv(filePath)).thenThrow(new IOException());

        CommonApiResponse response = iotDeviceDataService.loadCsvData(filePath);

        assertEquals("ERR-VOIS-JAVA-SDK-404", response.getResponseCode());
        assertEquals("ERROR: no data file found", response.getResponseMessage());
        assertFalse(iotDeviceDataService.checkDeviceDataAvailability());
    }

    @Test
    void testLoadCsvDataGeneralError() throws IOException {
        String filePath = "path/to/file.csv";
        when(csvUtil.parseCsv(filePath)).thenThrow(new RuntimeException());

        CommonApiResponse response = iotDeviceDataService.loadCsvData(filePath);

        assertEquals("ERR-VOIS-JAVA-SDK-500", response.getResponseCode());
        assertEquals("ERROR: A technical exception occurred.", response.getResponseMessage());
        assertFalse(iotDeviceDataService.checkDeviceDataAvailability());
    }

    @Test
    void testGetDeviceDataByProductId() {
        List<IotDeviceData> mockData = Arrays.asList(
                new IotDeviceData(1L, 1, "WG11155638", 1.0, 1.0,
                        0.9, "OFF", "OFF", "Active"),
                new IotDeviceData(1L, 1, "6900001001", 1.0, 1.0,
                        0.9, "OFF", "OFF", "Inactive")
        );
        iotDeviceDataService.loadCsvData("path/to/file.csv");
        iotDeviceDataService.events = mockData;

        List<IotDeviceData> result = iotDeviceDataService.getDeviceDataByProductId("WG11155638");

        assertEquals(1, result.size());
        assertEquals("WG11155638", result.get(0).getProductId());
    }

    @Test
    void testFindClosestDeviceDataBasedOnTimestamp() {
        List<IotDeviceData> mockData = Arrays.asList(
                new IotDeviceData(1L, 1, "WG11155638", 1.0, 1.0,
                        0.9, "OFF", "OFF", "Active"),
                new IotDeviceData(2L, 1, "6900001001", 1.0, 1.0,
                        0.9, "OFF", "OFF", "Inactive")
        );

        IotDeviceData result = iotDeviceDataService.findClosestDeviceDataBasedOnTimestamp(mockData, 1L);

        assertNotNull(result);
        assertEquals("WG11155638", result.getProductId());
    }

    @Test
    void testPrepareIotDeviceDataResponse() {
        IotDeviceData data = new IotDeviceData(1L, 1, "WG11155638", 1.0, 1.0,
                0.9, "OFF", "OFF", "Active");

        IotDeviceDataResponse response = iotDeviceDataService.prepareIotDeviceDataResponse(data);

        assertEquals("WG11155638", response.getId());
        assertEquals(ProductType.getProductName("WG11155638"), response.getName());
        assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(1L), ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), response.getDatetime());
        assertEquals("1.0", response.getLongitude());
        assertEquals("1.0", response.getLatitude());
        assertEquals("Active", response.getStatus());
        assertEquals("High", response.getBattery());
        assertEquals("SUCCESS: Location identified.", response.getDescription());
    }
}
