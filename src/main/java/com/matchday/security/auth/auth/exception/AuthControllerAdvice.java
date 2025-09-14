package com.matchday.security.auth.auth.exception;

import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.common.exception.GeneralException;

public class AuthControllerAdvice extends GeneralException {
    public AuthControllerAdvice(ResponseCode responseCode) {
        super(responseCode);
    }
}