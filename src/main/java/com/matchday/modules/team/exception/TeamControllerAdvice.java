package com.matchday.modules.team.exception;

import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.common.exception.GeneralException;

public class TeamControllerAdvice extends GeneralException {
    public TeamControllerAdvice(ResponseCode responseCode) {
        super(responseCode);
    }

}