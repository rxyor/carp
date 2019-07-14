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
public class DuplicateDataException extends RuntimeException {

    static final long serialVersionUID = 3626215591603359027L;

    @Getter
    protected String msg;

    public DuplicateDataException() {
        this("duplicate data exception");
    }

    public DuplicateDataException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public DuplicateDataException(Throwable e) {
        super(e.getMessage());
        this.msg = e.getMessage();
    }

}
