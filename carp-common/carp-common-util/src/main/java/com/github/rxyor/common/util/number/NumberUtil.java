package com.github.rxyor.common.util.number;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019/3/13 Wed 17:05:00
 * @since 1.0.0
 */
public class NumberUtil {

    private NumberUtil() {
    }

    /**
     * 去除bigDecimal末尾的0
     *
     * @param bigDecimal 原数值
     * @param scale 要保留的位数, scale>=0
     * @param mode 舍入模式
     * @return 处理后的数值
     */
    public static BigDecimal removeTailZero(BigDecimal bigDecimal, int scale, RoundingMode mode) {
        if (bigDecimal == null) {
            throw new NullPointerException("BigDecimal不能为null");
        }

        if (scale < 0) {
            throw new IllegalArgumentException("scale不能小于0");
        }

        if (mode == null) {
            mode = RoundingMode.UP;
        }

        //bigDecimal转string
        String s = bigDecimal.setScale(scale, mode).toString();
        String[] strings = s.split("\\.");
        int len = strings.length;
        //取小数部分
        String decimalsPart = strings[len - 1];

        int newScale = decimalsPart.length();
        //反转小数部分字符串
        String reverseDecimalsPart = new StringBuilder(decimalsPart).reverse().toString();
        //重新计算保留位数
        for (Character c : reverseDecimalsPart.toCharArray()) {
            if ('0' == c) {
                newScale--;
            } else {
                break;
            }
        }

        return bigDecimal.setScale(newScale, mode);
    }

}
