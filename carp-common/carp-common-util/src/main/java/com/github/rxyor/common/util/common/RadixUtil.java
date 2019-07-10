package com.github.rxyor.common.util.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-04 Tue 16:52:00
 * @since 1.0.0
 */
public class RadixUtil {

    public final static char[] DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
        'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
        'Y', 'Z'};

    private final static Map<Character, Integer> DIGIT_MAP = new HashMap<>();

    static {
        for (int i = 0; i < DIGITS.length; i++) {
            DIGIT_MAP.put(DIGITS[i], i);
        }
    }

    /**
     * 支持的最大进制数
     */
    public static final int MAX_RADIX = DIGITS.length;

    /**
     * 支持的最小进制数
     */
    public static final int MIN_RADIX = 2;

    public static String convert2String(long i, int radix) {
        final int defaultRadix = 10;
        if (radix < MIN_RADIX || radix > MAX_RADIX) {
            radix = defaultRadix;
        }
        if (radix == defaultRadix) {
            return Long.toString(i);
        }

        final int size = 65;
        int charPos = 64;

        char[] buf = new char[size];
        boolean negative = (i < 0);

        if (!negative) {
            i = -i;
        }

        while (i <= -radix) {
            buf[charPos--] = DIGITS[(int) (-(i % radix))];
            i = i / radix;
        }
        buf[charPos] = DIGITS[(int) (-i)];

        if (negative) {
            buf[--charPos] = '-';
        }
        return new String(buf, charPos, (size - charPos));
    }

    public static long convert2Number(String s, int radix) {
        Optional.ofNullable(s).orElseThrow(() -> new NumberFormatException("for input string: null"));

        if (radix < MIN_RADIX) {
            throw new NumberFormatException("radix " + radix
                + " less than " + RadixUtil.MAX_RADIX);
        }
        if (radix > MAX_RADIX) {
            throw new NumberFormatException("radix " + radix
                + " greater than " + RadixUtil.MIN_RADIX);
        }

        long result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        long limit = -Long.MAX_VALUE;
        long multiplyMin;
        Integer digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') {
                if (firstChar == '-') {
                    negative = true;
                    limit = Long.MIN_VALUE;
                } else if (firstChar != '+') {
                    throw numberFormatException(s);
                }

                if (len == 1) {
                    throw numberFormatException(s);
                }
                i++;
            }
            multiplyMin = limit / radix;
            while (i < len) {
                digit = DIGIT_MAP.get(s.charAt(i++));
                if (digit == null) {
                    throw numberFormatException(s);
                }
                if (digit < 0) {
                    throw numberFormatException(s);
                }
                if (result < multiplyMin) {
                    throw numberFormatException(s);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw numberFormatException(s);
                }
                result -= digit;
            }
        } else {
            throw numberFormatException(s);
        }
        return negative ? result : -result;
    }

    private static NumberFormatException numberFormatException(String s) {
        return new NumberFormatException("For input string: \"" + s + "\"");
    }
}
