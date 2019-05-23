package com.github.rxyor.distributed.delay;

import com.github.rxyor.common.util.ThreadUtil;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-22 Wed 15:57:00
 * @since 1.0.0
 */
public class DelayScannerTest extends DelayBaseTest {

    @Before
    public void beforeStartup() {
        DelayGlobalConfig.addHandler(new PeopleJobHandler());
        DelayGlobalConfig.addHandler(new PhoneJobHandler());
    }

    @Test
    public void startup() {
        DelayScanner.startup();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            DelayQueue<People> peopleDelayQueue = new DelayQueue<>("People");
            People people = new People();
            people.name = "name " + i;
            people.age = i % 18;
            peopleDelayQueue.offer(people, i % 100L + 1L);

            DelayQueue<Phone> delayQueue = new DelayQueue<>("Phone");
            Phone phone = new Phone();
            phone.brand = "brand " + i;
            phone.price = (i % 3 + 1) * 1000 + 88;
            delayQueue.offer(phone, i % 100L + 1L);

            ThreadUtil.sleepSeconds(2L);
        }
    }

    @Data
    public class People {

        private String name;

        private Integer age;
    }

    @Data
    public class Phone {

        private String brand;

        private Integer price;
    }

    public class PeopleJobHandler extends AbstractDelayJobHandler {

        @Override
        public void handleDelayJob(DelayJob delayJob) {
            System.out.println(System.currentTimeMillis() / 1000 + "<people job>: " + delayJob);
        }

        @Override
        public List<String> getProcessibleTopics() {
            return Lists.newArrayList("People");
        }
    }

    public class PhoneJobHandler extends AbstractDelayJobHandler {

        @Override
        public void handleDelayJob(DelayJob delayJob) {
            System.out.println(System.currentTimeMillis() / 1000 + "<phone job>: " + delayJob);
        }

        @Override
        public List<String> getProcessibleTopics() {
            return Lists.newArrayList("Phone");
        }
    }

}
