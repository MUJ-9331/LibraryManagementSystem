package library.util;

import library.exception.CustomException;

public class Result<T> {
    private boolean success;
    private String message;
    private T data;
    private int code;

    private Result(boolean success, String message, T data, int code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    //<T>：声明这是一个泛型方法，T是一个类型占位符
    public static <T> Result<T> success(T data) {
        return new Result<>(true, "操作成功", data, 200);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, message, data, 200);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(false, message, null, 500);
    }

    public static <T> Result<T> error(String message, int code) {
        return new Result<>(false, message, null, code);
    }

    public static <T> Result<T> fromException(CustomException e) {
        return new Result<>(false, e.getMessage(), null, e.getErrorCode().getCode());
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public int getCode() {
        return code;
    }
}