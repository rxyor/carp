package com.github.rxyor.common.core.exception;

import lombok.ToString;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-14 Tue 16:56:00
 * @since 1.0.0
 */
@ToString(callSuper = true)
public class ReadFileException extends CarpIOException {

    static final long serialVersionUID = -7530132881684972853L;

    public ReadFileException() {
        this("read file exception");
    }

    public ReadFileException(String msg) {
        super(msg);
    }

    public ReadFileException(Throwable e) {
        super(e);
    }
}
