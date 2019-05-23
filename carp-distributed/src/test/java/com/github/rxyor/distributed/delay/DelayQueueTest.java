package com.github.rxyor.distributed.delay;

import com.github.rxyor.common.util.SnowFlakeHelper;
import java.util.List;
import lombok.Data;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-20 Mon 17:32:00
 * @since 1.0.0
 */
public class DelayQueueTest extends DelayBaseTest {


    private DelayQueue delayQueue=new DelayQueue("Person");

    @Test
    public void offer() {
        Person person = new Person();
        person.setAge(18);
        person.setName("陈悠");
        DelayJob delayJob = new DelayJob();
        delayJob.setTopic("Test");
        delayJob.setExecTime(System.currentTimeMillis() + 600 * 1000L);
        delayJob.setId(SnowFlakeHelper.builder().build().nextHexId());
        delayJob.setBody(person);

        delayQueue.offer(person, 600 * 1000L);
    }

    @Test
    public void peek() {
        List<DelayJob<Person>> list = delayQueue.peek();
        for (DelayJob<Person> delayJob : list) {
            Person person = delayJob.getBody();
            System.out.println(person);
        }
    }

    @Data
    public class Person {

        private String name;

        private Integer age;
    }
}