package com.vois.iot.java.rest.sdk.util;

import com.vois.iot.java.rest.sdk.model.IotDeviceData;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class CsvUtilTest {

    @Mock
    private CSVParser csvParserMock;

    @Mock
    private CSVRecord csvRecordMock1;

    @Mock
    private CSVRecord csvRecordMock2;

    private CsvUtil csvUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        csvUtil = new CsvUtil();
    }

    @Test
    void testParseCsvRecords_Success() {
        // Mock CSVRecords
        when(csvRecordMock1.get("DateTime")).thenReturn("1625078400000");
        when(csvRecordMock1.get("EventId")).thenReturn("1");
        when(csvRecordMock1.get("ProductId")).thenReturn("ProductA");
        when(csvRecordMock1.get("Latitude")).thenReturn("52.5200");
        when(csvRecordMock1.get("Longitude")).thenReturn("13.4050");
        when(csvRecordMock1.get("Battery")).thenReturn("75.0");
        when(csvRecordMock1.get("Light")).thenReturn("LightA");
        when(csvRecordMock1.get("AirplaneMode")).thenReturn("On");

        when(csvRecordMock2.get("DateTime")).thenReturn("1625078500000");
        when(csvRecordMock2.get("EventId")).thenReturn("2");
        when(csvRecordMock2.get("ProductId")).thenReturn("ProductB");
        when(csvRecordMock2.get("Latitude")).thenReturn("");
        when(csvRecordMock2.get("Longitude")).thenReturn("13.4050");
        when(csvRecordMock2.get("Battery")).thenReturn("50.0");
        when(csvRecordMock2.get("Light")).thenReturn("LightB");
        when(csvRecordMock2.get("AirplaneMode")).thenReturn("Off");

        // Mock CSVParser
        when(csvParserMock.iterator()).thenReturn(Arrays.asList(csvRecordMock1, csvRecordMock2).iterator());

        // Inject the mock CSVParser into the method
        List<IotDeviceData> result = csvUtil.parseCsvRecords(csvParserMock);

        // Verify the results
        assertEquals(2, result.size());

        IotDeviceData data1 = result.get(0);
        assertEquals(1625078400000L, data1.getDateTime());
        assertEquals(1, data1.getEventId());
        assertEquals("ProductA", data1.getProductId());
        assertEquals(52.5200, data1.getLatitude());
        assertEquals(13.4050, data1.getLongitude());
        assertEquals(75.0, data1.getBattery());
        assertEquals("LightA", data1.getLight());
        assertEquals("On", data1.getAirplaneMode());

        IotDeviceData data2 = result.get(1);
        assertEquals(1625078500000L, data2.getDateTime());
        assertEquals(2, data2.getEventId());
        assertEquals("ProductB", data2.getProductId());
        assertEquals(0, data2.getLatitude());
        assertEquals(13.4050, data2.getLongitude());
        assertEquals(50.0, data2.getBattery());
        assertEquals("LightB", data2.getLight());
        assertEquals("Off", data2.getAirplaneMode());
    }

    @Test
    void testParseCsv_FileNotFound() {
        assertThrows(IOException.class, () -> csvUtil.parseCsv("non_existent.csv"));
    }

    @Test
    void testParseCsvRecords_InvalidData() {
        // Mock CSVRecord with invalid data
        when(csvRecordMock1.get("DateTime")).thenReturn("invalid");

        // Mock CSVParser
        when(csvParserMock.iterator()).thenReturn(List.of(csvRecordMock1).iterator());

        assertThrows(NumberFormatException.class, () -> csvUtil.parseCsvRecords(csvParserMock));
    }
}
