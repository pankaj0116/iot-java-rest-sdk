package com.vois.iot.java.rest.sdk.exception;

import com.vois.iot.java.rest.sdk.model.CommonApiResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SdkGlobalExceptionHandlerTest {

    SdkGlobalExceptionHandler exceptionHandler = new SdkGlobalExceptionHandler();

    @Test
    void testHandleNoResourceFoundException() {
        NoResourceFoundException ex = Mockito.mock(NoResourceFoundException.class);
        ResponseEntity<CommonApiResponse> response = exceptionHandler.handleException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404", response.getBody().getResponseCode());
        assertEquals("Invalid Url", response.getBody().getResponseMessage());
    }

    @Test
    void testHandleMissingServletRequestParameterException() {
        MissingServletRequestParameterException ex = Mockito.mock(MissingServletRequestParameterException.class);
        ResponseEntity<CommonApiResponse> response = exceptionHandler.handleException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("404", response.getBody().getResponseCode());
        assertEquals("Required request parameters are missing", response.getBody().getResponseMessage());
    }

    @Test
    void testHandleException() {
        Exception ex = Mockito.mock(Exception.class);
        ResponseEntity<CommonApiResponse> response = exceptionHandler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERR-VOIS-JAVA-SDK-999", response.getBody().getResponseCode());
        assertEquals("A technical exception occurred at server side.", response.getBody().getResponseMessage());
    }
}

