package com.matchday.common.exception.handler;


import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.matchday.common.entity.BaseResponse;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.common.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class MasterExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        return handleExceptionInternal(e, ResponseCode._UNAUTHORIZED, request);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> general(GeneralException e, WebRequest request) {
        return handleExceptionInternal(e, e.getErrorCode(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> formatException(HttpMessageNotReadableException e, WebRequest request) {
        Throwable root = getRootCause(e);
        if (root instanceof DateTimeParseException) {
            return handleExceptionInternal(e, ResponseCode._INVALID_FORMAT, request);
        } else if (root instanceof MismatchedInputException) {
            return handleExceptionInternal(e, ResponseCode._MISMATCHED_INPUT, request);

        }
        return handleExceptionInternal(e, ResponseCode._BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        e.printStackTrace(); // 클라이언트에게 불필요한 정보를 노출할 수 있으므로 삭제
        return handleExceptionInternalFalse(e, ResponseCode._INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, ResponseCode._INTERNAL_SERVER_ERROR.getHttpStatus(),request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> processValidationError(MethodArgumentNotValidException exception) {

        final List<BaseResponse.FieldError> fieldErrors = getFieldErrors(exception.getBindingResult());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildFieldErrors(ResponseCode._BAD_REQUEST, fieldErrors));

    }

    private List<BaseResponse.FieldError> getFieldErrors(BindingResult bindingResult) {
        final List<FieldError> errors = bindingResult.getFieldErrors();
        return errors.parallelStream()
                .map(error -> BaseResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage()).build())
                .collect(Collectors.toList());
    }

    private BaseResponse buildFieldErrors(ResponseCode responseCode, List<BaseResponse.FieldError> errors) {
        return BaseResponse.builder()
                .isSuccess(false)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .result(errors)
                .build();
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, ResponseCode errorCode,
                                                           WebRequest request) {
        return handleExceptionInternal(e, errorCode, HttpHeaders.EMPTY, errorCode.getHttpStatus(),
                request);
    }


    private ResponseEntity<Object> handleExceptionInternal(Exception e, ResponseCode errorCode,
                                                           HttpHeaders headers, HttpStatus status, WebRequest request) {
        BaseResponse<Object> body = BaseResponse.onFailure(null, errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(body);
    }

    private ResponseEntity<Object> handleExceptionInternalFalse(Exception e, ResponseCode errorCode,
                                                                HttpHeaders headers, HttpStatus status, WebRequest request) {
        BaseResponse<Object> body = BaseResponse.onFailure(null, errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(body);
    }

    private Throwable getRootCause(Throwable ex) {
        Throwable result = ex;
        while (result.getCause() != null) {
            result = result.getCause();
        }
        return result;
    }

}