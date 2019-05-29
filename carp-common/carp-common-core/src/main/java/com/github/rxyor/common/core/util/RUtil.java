package com.github.rxyor.common.core.util;


import com.github.rxyor.common.core.enums.RCodeEnum;
import com.github.rxyor.common.core.model.R;
import java.util.Objects;

/**
 *<p>
 *ResponseUtil
 *</p>
 *
 * @author liuyang
 * @date 2018-12-06 Thu 00:13:26
 * @since 1.0.0
 */
public class RUtil {

    private RUtil() {
    }

    public static <T> R<T> success() {
        return new R<T>()
            .success(true)
            .code(RCodeEnum.SUCCESS.getCode())
            .msg(RCodeEnum.SUCCESS.getMsg());
    }

    public static <T> R<T> success(T data) {
        return new R<T>()
            .success(true)
            .code(RCodeEnum.SUCCESS.getCode())
            .msg(RCodeEnum.SUCCESS.getMsg())
            .data(data);
    }

    public static <T> R<T> fail() {
        return new R<T>()
            .success(false)
            .code(RCodeEnum.FAIL.getCode())
            .msg(RCodeEnum.FAIL.getMsg());
    }

    public static <T> R<T> fail(Exception e) {
        Objects.requireNonNull(e);
        return new R<T>()
            .success(false)
            .code(RCodeEnum.FAIL.getCode())
            .msg(e.getMessage());
    }

    public static <T> R<T> fail(Integer errorCode, String errorMsg) {
        return new R<T>()
            .success(false)
            .code(errorCode)
            .msg(errorMsg);
    }

    public static <T> R<T> fail(Integer errorCode, String errorMsg, T data) {
        return new R<T>()
            .success(false)
            .code(errorCode)
            .msg(errorMsg)
            .data(data);
    }

    public static <T> boolean isRequestSuccess(R<T> result) {
        return result != null && result.getSuccess();
    }

    public static <T> boolean isRequestSuccessCanNotNullData(R<T> result) {
        return result != null && result.getSuccess() && result.getData() != null;
    }
}
