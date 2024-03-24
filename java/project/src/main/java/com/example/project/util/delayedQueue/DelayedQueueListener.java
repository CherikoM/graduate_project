package com.example.project.util.delayedQueue;

/**
 * 队列事件监听接口，需要实现这个方法
 *
 * @param <T>
 */
public interface DelayedQueueListener<T> {
    /**
     * 执行方法
     *
     * @param t
     */
    void invoke(T t);
}