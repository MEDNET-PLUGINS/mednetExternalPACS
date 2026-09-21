package com.mednet.externalpacs.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class APIResponseEntityUtil {

    public static ResponseEntity<String> createErrorResponseEntity(int errorCode, String errorMsg) {
        APIError apiError = new APIError();
        apiError.setErrorCode(errorCode);
        apiError.setErrorMessage(errorMsg);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(false);
        apiResponse.setData(apiError);

        ObjectMapper objectMapper = createMapper();
        try {
            return new ResponseEntity<>(objectMapper.writeValueAsString(apiResponse), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static ResponseEntity<String> createSuccessResponseEntity(Object successMsg) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setData(successMsg);
        return new ResponseEntity<>(createMapper().writeValueAsString(apiResponse), HttpStatus.OK);
    }

    private static ObjectMapper createMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
