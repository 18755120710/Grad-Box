package com.material.manaement.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result handle(Exception e) {
        e.printStackTrace();
        return Result.failed(e.getMessage());
    }
    
    @ExceptionHandler(value = ApiException.class)
    public Result handle(ApiException e) {
        if (e.getResultCode() != null) {
            return Result.failed(e.getResultCode());
        }
        return Result.failed(e.getMessage());
    }
}
