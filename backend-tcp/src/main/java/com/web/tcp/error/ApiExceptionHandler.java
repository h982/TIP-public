package com.web.tcp.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<ErrorMessage> handleCustomException(CustomException e) {
        log.error("handle CustomException throw Exception : {}", e.getErrorCode());
        return ErrorMessage.errorToMessage(e.getErrorCode());
    }
}
