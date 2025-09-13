package com.matchday.match.exception.advice;

import com.matchday.global.entity.enums.ResponseCode;
import com.matchday.global.exception.GeneralException;

public class MatchControllerAdvice extends GeneralException {

    public MatchControllerAdvice(ResponseCode responseCode) {
        super(responseCode);
    }

}
