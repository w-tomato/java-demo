package com.bradaier.javademo.queue;

import java.util.LinkedList;
import java.util.Random;

/**
 * @author w-tomato
 * @description 生产者消费者队列  wait/notify实现版本
 * @date 2023/5/22
 */
public class ProducerConsumerQueue {

    private static final int CAPACITY = 10;
    private static LinkedList<Integer> queue = new LinkedList<>();

    // 生产者，如果队列满了，等待消费者消费，如果成功生产，通知消费者消费
    public synchronized void producer() throws InterruptedException {
        if (queue.size() == CAPACITY) {
            System.out.println("队列满了，等待消费者消费");
            wait();
        }
        queue.add(new Random().nextInt(100));
        Thread.sleep(1000);
        notify();
    }

    // 消费者，如果队列空了，等待生产者生产，如果成功消费，通知生产者生产
    public synchronized Integer consumer() throws InterruptedException {
        if (queue.size() == 0) {
            System.out.println("队列空了，等待生产者生产");
            wait();
        }
        Integer remove = queue.remove();
        Thread.sleep(1000);
        notify();
        return remove;
    }

    public static void main(String[] args) {
        ProducerConsumerQueue producerConsumerQueue = new ProducerConsumerQueue();

        new Thread(() -> {
            try {
                while (true) {
                    producerConsumerQueue.producer();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                while (true) {
                    Integer consumer = producerConsumerQueue.consumer();
                    System.out.println("消费者消费了：" + consumer);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

}
