package com.github.rxyor.common.util.xlsx.test;

import com.github.rxyor.common.util.xlsx.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-14 Sun 03:52:00
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Girl extends Human {

    @Column(index = 0, title = "姓名")
    private String name;

    @Column(index = 1, title = "年龄")
    private Integer age;

    @Column(index = 2, title = "身高")
    private Float height;

    @Column(index = 2, title = "体重")
    private Float weight;
}
