package com.samratalam.ewallet_system.exception.advice;

import com.samratalam.ewallet_system.dto.BaseResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.util.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<BaseResponse> handleException(
            EntityNotFoundException ex
    ) {
        log.error(ex.getMessage(), ex);
        return buildBadRequest(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(
            Exception ex
    ) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(BaseResponse.failedResponse(ex.getMessage()));
    }

    private ResponseEntity<BaseResponse> buildBadRequest(String message) {
        return ResponseEntity.badRequest().body(BaseResponse.failedResponse(message));
    }
}
