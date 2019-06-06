package com.github.rxyor.webmvc.web.controller;

import com.github.rxyor.common.core.model.R;
import com.github.rxyor.common.core.util.RUtil;
import com.github.rxyor.common.util.TimeUtil;
import com.github.rxyor.distributed.redisson.delay.core.DelayClientProxy;
import com.github.rxyor.distributed.redisson.delay.core.DelayJob;
import com.github.rxyor.distributed.redisson.delay.core.ScanWrapper;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-29 Wed 14:56:00
 * @since 1.0.0
 */
@Controller
@RequestMapping("/delay")
public class DelayJobController {

    @Autowired
    private ScanWrapper scanWrapper;

    @PostMapping("/job/add")
    @ResponseBody
    public R addDelayJob(@RequestBody DelayJob<Map<String, Object>> delayJob) {
        if (delayJob.getExecTime() == null) {
            delayJob.setExecTime(TimeUtil.getCurrentSeconds() + 10);
        }
        DelayClientProxy client = scanWrapper.getDelayClientProxy();
        client.offer(delayJob);
        return RUtil.success(delayJob);
    }
}
