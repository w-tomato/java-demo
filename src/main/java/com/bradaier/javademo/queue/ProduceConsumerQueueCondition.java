package com.bradaier.javademo.queue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProduceConsumerQueueCondition {

    private static final int MAX_CAPACITY = 10;
    private final Queue<Integer> queue = new LinkedList<>();

    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public void produce(int num) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == MAX_CAPACITY) {
                System.out.println("队列已满，生产者进入等待状态...");
                notFull.await();
            }
            queue.add(num);
            System.out.println("生产者生产了：" + num);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void consume() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                System.out.println("队列为空，消费者进入等待状态...");
                notEmpty.await();
            }
            int num = queue.poll();
            System.out.println("消费者消费了：" + num);
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ProduceConsumerQueueCondition demo = new ProduceConsumerQueueCondition();
        Thread producerThread = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                try {
                    demo.produce(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread consumerThread = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                try {
                    demo.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        producerThread.start();
        consumerThread.start();
    }
}
