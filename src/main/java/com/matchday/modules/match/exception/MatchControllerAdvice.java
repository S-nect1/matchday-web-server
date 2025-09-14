package com.matchday.modules.match.exception;

import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.common.exception.GeneralException;

public class MatchControllerAdvice extends GeneralException {

    public MatchControllerAdvice(ResponseCode responseCode) {
        super(responseCode);
    }

}
