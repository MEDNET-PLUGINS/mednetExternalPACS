package com.mednet.externalpacs.common.util;

import lombok.Data;

@Data
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    public ApiResponse() {
        this.data = new Object();
    }

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data != null ? data : new Object();
    }
}
