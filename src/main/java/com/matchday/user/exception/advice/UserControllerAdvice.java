package com.matchday.user.exception.advice;


import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.global.exception.GeneralException;

public class UserControllerAdvice extends GeneralException {
    public UserControllerAdvice(ResponseCode responseCode) {
        super(responseCode);
    }
}