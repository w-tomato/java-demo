package com.bradaier.javademo.tests;

import java.util.concurrent.atomic.AtomicStampedReference;

public class CASDemo {
    private static AtomicStampedReference<Pair> data = new AtomicStampedReference<>(new Pair(0, 0), 0);

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                int stamp = data.getStamp();
                Pair prev = data.getReference();
                Pair next = new Pair(prev.getX() + 1, prev.getY() + 1);
                if (data.compareAndSet(prev, next, stamp, stamp + 1)) {
                    System.out.println("Thread 1 updated data to " + next);
                } else {
                    System.out.println("Thread 1 failed to update data");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                int stamp = data.getStamp();
                Pair prev = data.getReference();
                Pair next = new Pair(prev.getX() - 1, prev.getY() - 1);
                if (data.compareAndSet(prev, next, stamp, stamp + 1)) {
                    System.out.println("Thread 2 updated data to " + next);
                } else {
                    System.out.println("Thread 2 failed to update data");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    static class Pair {
        private int x;
        private int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "[" + x + "," + y + "]";
        }
    }
}
