package com.example.project.util.delayedQueue;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DelayedQueue {

    @Autowired
    private RedissonClient redissonClient;

    private static Logger logger = LoggerFactory.getLogger(DelayedQueue.class);

    /**
     * 添加队列
     *
     * @param t        DTO传输类
     * @param delay    时间数量
     * @param timeUnit 时间单位
     * @param <T>      泛型
     */
    public <T> void addQueue(T t, long delay, TimeUnit timeUnit, String queueName) {
        logger.info("添加队列{},delay:{},timeUnit:{}", queueName, delay, timeUnit);
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(t, delay, timeUnit);
    }

}