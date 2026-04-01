package com.material.manaement.common;

public class ApiException extends RuntimeException {
    private ResultCode resultCode;

    public ApiException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public ApiException(String message) {
        super(message);
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
