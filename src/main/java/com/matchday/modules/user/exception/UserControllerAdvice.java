package com.matchday.modules.user.exception;


import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.common.exception.GeneralException;

public class UserControllerAdvice extends GeneralException {
    public UserControllerAdvice(ResponseCode responseCode) {
        super(responseCode);
    }
}