package com.matchday.external.exception;

import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.common.exception.GeneralException;

public class ExternalControllerAdvice extends GeneralException {
    public ExternalControllerAdvice(ResponseCode responseCode) {
        super(responseCode);
    }
}
