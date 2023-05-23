package com.bradaier.javademo.queue;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author w-tomato
 * @description Condition版本的生产者消费者模式
 * @date 2023/5/23
 */
public class ProduceConsumerQueueCondition {

    private static final int MAX_SIZE = 10;
    private static final LinkedList<Integer> queue = new LinkedList<>();
    // 这里用可重入锁其实没有用到可重入特性，只是为了用Condition
    private static final Lock lock = new ReentrantLock();
    // 这里用两个Condition，一个用于生产者，一个用于消费者，这样就不用notifyAll了
    // 最主要避免了虚假唤醒，也就是说，生产者只唤醒消费者，消费者只唤醒生产者，如果用wait/notify，生产者有可能唤醒的还是生产者，这样就会出现虚假唤醒
    private static final Condition notFull = lock.newCondition();
    private static final Condition notEmpty = lock.newCondition();

    // 生产者，依然是如果队列满了，等待消费者消费，如果成功生产，通知消费者消费
    public void producer() {
        lock.lock();
        try {
            if (queue.size() == MAX_SIZE) {
                System.out.println("队列满了，等待消费者消费");
                notFull.await();
            }
            int good = new Random().nextInt(100);
            queue.add(good);
            System.out.println("生产者生产了：" + good + " 当前队列大小：" + queue.size());
            Thread.sleep(1000);
            notEmpty.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    // 消费者，依然是如果队列空了，等待生产者生产，如果成功消费，通知生产者生产
    public int consumer() {
        lock.lock();
        try {
            if (queue.size() == 0) {
                System.out.println("队列空了，等待生产者生产");
                notEmpty.await();
            }
            Integer remove = queue.remove();
            System.out.println("消费者消费了：" + remove + " 当前队列大小：" + queue.size());
            Thread.sleep(1000);
            notFull.signalAll();
            return remove;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ProduceConsumerQueueCondition produceConsumerQueueCondition = new ProduceConsumerQueueCondition();

        new Thread(() -> {
            while (true) {
                produceConsumerQueueCondition.producer();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                produceConsumerQueueCondition.consumer();
            }
        }).start();
    }


}
