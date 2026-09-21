package com.mednet.externalpacs.common.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class APIError {
    private Integer errorCode;
    private String errorMessage;
}
