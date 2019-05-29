package com.github.rxyor.common.core.enums;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *<p>
 *响应或调用结果异常码枚举
 *</p>
 *
 * @author liuyang
 * @date 2019/3/7 Thu 13:58:00
 * @since 1.0.0
 */
@SuppressWarnings("all")
@ToString
public enum RCodeEnum {
    SUCCESS(200, "请求成功"),
    FAIL(500, "请求失败"),
    ERROR_SYSTEM(500, "系统异常"),
    BIZ_ERROR(501, "业务系统异常"),
    ERROR_NO_USER(531, "，获取用户信息失败，请重新登录再试"),
    ERROR_UTIL_USE(562, "工具类异常，请稍后再试"),
    DB_OPERATOR_ERROR(570, "数据库操作失败"),
    ACCESS_DENY(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    PARAM_ERROR(430, "参数错误"),
    PARAM_LACK(431, "参数缺少"),
    LOGIN_FAIL(432, "登录失败，用户名或密码错误"),
    USER_NOT_EXIST(601, "用户不存在"),
    RESULT_ERROR(700, "返回数据有误"),
    RESULT_DIRTY_DATA(701, "返回数据含脏数据"),
    ;

    /**
     * 响应Code
     */
    @Getter
    @Setter
    private Integer code;
    /**
     * 响应消息
     */
    @Getter
    @Setter
    private String msg;

    RCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
