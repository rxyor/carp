package com.github.rxyor.common.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *<p>
 *Response
 *</p>
 *
 * @author liuyang
 * @date 2018-12-05 Wed 22:46:48
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
public class R<T> {

    private Boolean success = false;
    private Integer code;
    private String msg;
    private T data;
    private String traceId;
    private Long timestamp;

    public R() {
        this.timestamp = System.currentTimeMillis();
        this.traceId = Integer.toHexString(this.timestamp.intValue());
    }

    public R<T> success(boolean success) {
        this.success = success;
        return this;
    }

    public R<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    public R<T> timestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public R<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public R<T> traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

}
