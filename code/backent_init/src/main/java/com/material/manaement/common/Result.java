package com.material.manaement.common;

public class Result<T> {
    private long code;
    private String message;
    private T data;

    protected Result() {
    }

    protected Result(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> failed(ResultCode resultCode) {
        return new Result<T>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> Result<T> failed(String message) {
        return new Result<T>(ResultCode.FAILED.getCode(), message, null);
    }

    public static <T> Result<T> failed() {
        return failed(ResultCode.FAILED);
    }

    public static <T> Result<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    public static <T> Result<T> validateFailed(String message) {
        return new Result<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    public static <T> Result<T> unauthorized(T data) {
        return new Result<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    public static <T> Result<T> forbidden(T data) {
        return new Result<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }
}
