package com.matchday.external.exception;

import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.global.exception.GeneralException;

public class ExternalControllerAdvice extends GeneralException {
    public ExternalControllerAdvice(ResponseCode responseCode) {
        super(responseCode);
    }
}
