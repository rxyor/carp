package com.github.rxyor.example.spring.mvc.delayqueue.controller;

import com.github.rxyor.common.core.model.R;
import com.github.rxyor.common.core.util.RUtil;
import com.github.rxyor.distributed.redisson.delay.core.DelayClientProxy;
import com.github.rxyor.distributed.redisson.delay.core.DelayJob;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-29 Wed 14:56:00
 * @since 1.0.0
 */
@Slf4j
@Api(value = "延时队列")
@RestController
@AllArgsConstructor
@RequestMapping("/delay")
public class DelayJobController {

    private final DelayClientProxy delayClientProxy;

    @ApiOperation(value = "添加一个延时任务", httpMethod = "POST")
    @PostMapping("/job/add")
    @ResponseBody
    public R addDelayJob(@RequestBody DelayJob<Map<String, Object>> delayJob) {
        log.info("received a job ,current time is:{}", System.currentTimeMillis() / 1000);
        if (delayJob.getExecTime() == null) {
            delayJob.setExecTime(System.currentTimeMillis() / 1000 + 10);
        }
        delayClientProxy.offer(delayJob);
        return RUtil.success(delayJob);
    }
}
