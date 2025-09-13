package com.matchday.team.exception.advice;

import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.global.exception.GeneralException;

public class TeamControllerAdvice extends GeneralException {
    public TeamControllerAdvice(ResponseCode responseCode) {
        super(responseCode);
    }

}