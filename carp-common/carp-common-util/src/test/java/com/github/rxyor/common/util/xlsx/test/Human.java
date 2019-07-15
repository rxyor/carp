package com.github.rxyor.common.util.xlsx.test;

import com.github.rxyor.common.util.xlsx.Column;
import lombok.Data;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-15 Mon 10:52:00
 * @since 1.0.0
 */
@Data
public class Human {

    @Column(title = "眼睛颜色")
    private String eyeColor;

    @Column(title = "头发颜色")
    private String hairColor;

}
