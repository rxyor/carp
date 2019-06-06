package com.github.rxyor.example.xsd.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-06 Thu 00:55:00
 * @since 1.0.0
 */
public class ThreadPool {

    public static void main(String[] args) {
        LinkedBlockingDeque<Runnable> deque = new LinkedBlockingDeque(1024);
        ExecutorService executors = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, deque);
        executors.submit(() -> System.out.println("inner task"));
        while (true) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i = 10;
            while (i-- > 0) {
//                executors.submit(() -> System.out.println("submit task"));
                deque.offer(() -> System.out.println("queue add task"));
            }
        }
    }


}
