package com.matchday.auth.exception.advice;

import com.matchday.auth.controller.AuthController;
import com.matchday.global.entity.BaseResponse;
import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.global.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class AuthControllerAdvice extends GeneralException {
    public AuthControllerAdvice(ResponseCode responseCode) {
        super(responseCode);
    }
}