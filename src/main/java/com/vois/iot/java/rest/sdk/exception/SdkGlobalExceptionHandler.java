package com.vois.iot.java.rest.sdk.exception;

import com.vois.iot.java.rest.sdk.model.CommonApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * SdkGlobalExceptionHandler
 * We can Add Specific Exceptions handling here with custom responses.
 */
@Slf4j
@ControllerAdvice
public class SdkGlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CommonApiResponse> handleException(NoResourceFoundException e) {
        log.info("SdkGlobalExceptionHandler :: handleException method :: NoResourceFoundException ");

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CommonApiResponse("404", "Invalid Url"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonApiResponse> handleException(MissingServletRequestParameterException e) {
        log.info("SdkGlobalExceptionHandler :: handleException method :: MissingServletRequestParameterException ");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CommonApiResponse("404",
                        "Required request parameters are missing"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonApiResponse> handleException(Exception e) {
        log.info("SdkGlobalExceptionHandler :: handleException method :: Exception ");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CommonApiResponse("ERR-VOIS-JAVA-SDK-999",
                        "A technical exception occurred at server side."));
    }
}
