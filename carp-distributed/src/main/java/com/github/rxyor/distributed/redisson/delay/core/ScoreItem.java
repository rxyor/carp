package com.github.rxyor.distributed.redisson.delay.core;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 16:23:00
 * @since 1.0.0
 */
@AllArgsConstructor
@Data
public class ScoreItem implements Serializable {

    private static final long serialVersionUID = 3284584517578270550L;

    public ScoreItem() {
    }

    /**
     * job id
     */
    private String id;

    /**
     * 时间戳(精确到秒)
     */
    private Long score;
}
