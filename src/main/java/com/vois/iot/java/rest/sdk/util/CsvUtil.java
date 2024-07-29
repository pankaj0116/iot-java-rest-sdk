package com.vois.iot.java.rest.sdk.util;

import com.vois.iot.java.rest.sdk.model.IotDeviceData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CsvUtil class to parse Csv file data
 */
@Slf4j
@Component
public class CsvUtil {

    public List<IotDeviceData> parseCsv(String filePath) throws IOException {
        log.info("CsvUtil :: parseCsv method ");
        try (FileReader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader()
                     .setSkipHeaderRecord(true)
                     .build())) {
            return parseCsvRecords(csvParser);
        }
    }

    public List<IotDeviceData> parseCsvRecords(CSVParser csvParser) {
        log.info("CsvUtil :: parseCsvRecords method ");

        List<IotDeviceData> deviceDataList = new ArrayList<>();
        for (CSVRecord csvRecord : csvParser) {
            IotDeviceData data = new IotDeviceData();
            data.setDateTime(Long.parseLong(csvRecord.get("DateTime")));
            data.setEventId(Integer.parseInt(csvRecord.get("EventId")));
            data.setProductId(csvRecord.get("ProductId"));
            data.setLatitude(csvRecord.get("Latitude").isEmpty() ? 0 : Double.parseDouble(csvRecord.get("Latitude")));
            data.setLongitude(csvRecord.get("Longitude").isEmpty() ? 0 : Double.parseDouble(csvRecord.get("Longitude")));
            data.setBattery(Double.parseDouble(csvRecord.get("Battery")));
            data.setLight(csvRecord.get("Light"));
            data.setAirplaneMode(csvRecord.get("AirplaneMode"));
            deviceDataList.add(data);
        }
        log.info("Total Count of Records in CSV file : {}", deviceDataList.size());
        return deviceDataList;
    }
}
