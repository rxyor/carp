package com.github.rxyor.common.core.exception;


import lombok.Getter;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-15 Wed 17:13:00
 * @since 1.0.0
 */
public class FileNotExistException extends RuntimeException {

    static final long serialVersionUID = 1794847147459793522L;

    @Getter
    protected String msg;

    public FileNotExistException() {
        this("file not exists");
    }

    public FileNotExistException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public FileNotExistException(Throwable e) {
        super(e.getMessage());
        this.msg = e.getMessage();
    }

}
