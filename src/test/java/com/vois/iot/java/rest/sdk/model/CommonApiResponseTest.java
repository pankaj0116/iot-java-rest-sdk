package com.vois.iot.java.rest.sdk.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonApiResponseTest {

    @Test
    void testConstructor() {
        String responseCode = "CODE-123";
        String responseMessage = "Success";

        CommonApiResponse response = new CommonApiResponse(responseCode, responseMessage);

        assertEquals(responseCode, response.getResponseCode());
        assertEquals(responseMessage, response.getResponseMessage());
    }
}
