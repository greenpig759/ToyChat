package com.example.toychat.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    private final Result result;
    private final T data;
    private final String message;
    private final String errorCode;

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(Result.SUCCESS, data, null, null);
    }

    public static <T> ApiResponse<T> success(T data, String message){
        return new ApiResponse<>(Result.SUCCESS, data, message, null);
    }

    public static ApiResponse<?> fail(String errorCode, String message){
        return new ApiResponse<>(Result.FAIL, null, message, errorCode);
    }

    public enum Result{
        SUCCESS, FAIL
    }
}
