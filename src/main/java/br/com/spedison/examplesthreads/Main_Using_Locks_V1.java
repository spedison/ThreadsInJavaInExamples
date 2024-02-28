package br.com.spedison.examplesthreads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class TaskToRunWithLock implements Runnable {

    static volatile int counter = 0;

    static Lock lock = new ReentrantLock();

    @Override
    public void run() {
        lock.lock(); // It's can use one or more locks.
        lock.lock();
        lock.lock();
        String name = Thread.currentThread().getName();
        System.out.println("Count %d in thread %s".formatted(++counter, name));
        lock.unlock(); // For each lock there are one unlock.
        lock.unlock();
        lock.unlock();
    }
}

public class Main_Using_Locks_V1 {
    public static void main(String[] args) {
        new Main_Using_Locks_V1().run(args);
    }

    public void run(String[] args) {
        try (ExecutorService es = Executors.newCachedThreadPool()) {
            var task = new TaskToRunWithLock();
            for (int i = 0; i < 6; i++) {
                es.execute(task);
            }
            es.shutdown();
            try {
                es.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
