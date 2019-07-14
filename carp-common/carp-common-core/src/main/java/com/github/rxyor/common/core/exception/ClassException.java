package com.github.rxyor.common.core.exception;


import lombok.Getter;
import lombok.ToString;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-15 Wed 17:13:00
 * @since 1.0.0
 */
@ToString(callSuper = true)
public class ClassException extends RuntimeException {

    static final long serialVersionUID = -1783905742222997373L;

    @Getter
    protected String msg;

    public ClassException() {
        this("io exception");
    }

    public ClassException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ClassException(Throwable e) {
        super(e.getMessage());
        this.msg = e.getMessage();
    }

}
