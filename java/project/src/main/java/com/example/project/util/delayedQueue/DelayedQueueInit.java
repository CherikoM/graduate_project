package com.example.project.util.delayedQueue;

import com.alibaba.fastjson2.JSON;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DelayedQueueInit implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(DelayedQueueInit.class);

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取应用上下文并获取相应的接口实现类
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, DelayedQueueListener> map = applicationContext.getBeansOfType(DelayedQueueListener.class);
        for (Map.Entry<String, DelayedQueueListener> taskEventListenerEntry : map.entrySet()) {
            String listenerName = taskEventListenerEntry.getValue().getClass().getName();
            startThread(listenerName, taskEventListenerEntry.getValue());
        }
    }

    /**
     * 启动线程获取队列*
     *
     * @param queueName                 queueName
     * @param redisDelayedQueueListener 任务回调监听
     * @param <T>                       泛型
     * @return
     */
    private <T> void startThread(String queueName, DelayedQueueListener redisDelayedQueueListener) {
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
        //由于此线程需要常驻，可以新建线程，不用交给线程池管理
        Thread thread = new Thread(() -> {
            logger.info("启动监听队列线程" + queueName);
            while (true) {
                try {
                    T t = blockingFairQueue.take();
                    logger.info("监听队列线程{},获取到值", queueName);
                    new Thread(() -> {
                        redisDelayedQueueListener.invoke(t);
                    }).start();
                } catch (Exception e) {
                    logger.info("监听队列线程错误,", e);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
        thread.setName(queueName);
        thread.start();
    }

}